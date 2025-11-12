package net.thechance.mena.faith.presentation.feature.quran.tilwah

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.reciter_deleted_successfully
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.service.DownloadSurahManager
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.ErrorState
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.args.TilawahSurahArgs
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.tilwah.args.TilawahArgs

class TilawahViewModel(
    val quranRepository: QuranRepository,
    private val tilawahArgs: TilawahArgs,
    snackBarHandler: SnackbarHandler,
) :
    BaseViewModel<TilawahUiState, TilawahEffect>(
        initialState = TilawahUiState(
            isSelectedShown = tilawahArgs.isSelectedShown,
            isSwipeable = tilawahArgs.isSwipeToDeleteEnabled,
        ),
        snackbarHandler = snackBarHandler,
    ), TilawahInteractionListener {
    class TilawahViewModel(
        private val quranRepository: QuranRepository,
        private val surahArgs: TilawahSurahArgs,
        private val downloadManager: DownloadSurahManager,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : BaseViewModel<TilawahUiState, TilawahEffect>(TilawahUiState(surahId = surahArgs.surahId)),
        TilawahInteractionListener {

        init {
            getAllReciters()
            updateDefaultReciter()
        }

        private fun updateDefaultReciter() {
            tryToExecute(
                execute = { quranRepository.getDefaultReciter() },
                onSuccess = { reciterId -> updateSelectedReciter(reciterId.first()) },
                onError = ::handleError
            )
        }

        override fun onBackClick() = sendEffect(TilawahEffect.NavigateBack)

        override fun onSearchClick() = sendEffect(TilawahEffect.NavigateToSearch)

        override fun onDownloadClick(reciterId: Int) {
            tryToExecute(
                execute = {
                    surahArgs.surahId?.let {
                        downloadAndCacheSurah(it, reciterId)

                    }
                },
                onSuccess = { onDownloadComplete(reciterId) },
                onError = ::handleError,
                dispatcher = dispatcher
            )
        }

        private suspend fun downloadAndCacheSurah(surahId: Int, reciterId: Int) {
            val remoteUrl = quranRepository.getRemoteSurahSoundUrl(
                surahId = surahId,
                reciterId = reciterId
            )

            val localPath = downloadManager.downloadSurahFile(
                url = remoteUrl,
                surahId = surahId,
                reciterId = reciterId
            )

            quranRepository.saveSurahAudioToCache(
                surahId = surahId,
                reciterId = reciterId,
                localPath = localPath
            )
        }

        override fun onSelectReciterClick(reciterId: Int) {
            tryToExecute(
                execute = { quranRepository.saveDefaultReciter(reciterId) },
                onSuccess = { updateSelectedReciter(reciterId) },
                onError = ::handleError
            )
        }

        override fun onDeleteReciterClick(reciterId: Int) {
            updateState {
                it.copy(
                    selectedReciterForDelete = reciterId,
                    showDeleteConfirmationDialog = true,
                )
            }
        }

        override fun onConfirmDeleteReciterClick() {
            tryToExecute(
                execute = { deleteSelectedReciter() },
                onSuccess = { showSuccessSnackBar() },
                onError = ::handleError
            )
        }

        override fun onDismissDeleteConfirmationDialog() {
            updateState {
                it.copy(
                    selectedReciterForDelete = null,
                    showDeleteConfirmationDialog = false
                )
            }
        }

        private fun showSuccessSnackBar() = snackbarHandler.showSnackBar(
            message = Res.string.reciter_deleted_successfully,
            status = SnackBarState.Status.Success,
            scope = viewModelScope,
        )

        private fun getAllReciters() {
            tryToExecute(
                execute = { quranRepository.getReciters() },
                onSuccess = ::getAllRecitersSuccessfully,
                dispatcher = dispatcher
            )
        }

        private fun updateSelectedReciter(reciterId: Int) {
            updateState { state ->
                state.copy(selectedReciterId = reciterId)
            }
        }

        private fun handleError(errorState: ErrorState) {
            println("Error: $errorState")
        }

        private fun getAllRecitersSuccessfully(reciters: List<Reciter>) {
            val filteredReciters = filterReciters(reciters)
            val surahId = surahArgs.surahId ?: return

            val recitersUi = filteredReciters.map {
                TilawahUiState.ReciterUi(
                    id = it.id,
                    name = it.name,
                    recitingType = it.tilawahType,
                    isDownloaded = quranRepository.isSurahAudioCached(surahId, reciter.id)
                )
            }

            updateState { it.copy(reciters = recitersUi) }
        }

        private fun deleteSelectedReciter() {
            updateState { state ->
                val newReciters =
                    state.reciters - state.reciters.first { it.id == state.selectedReciterForDelete }
                state.copy(
                    reciters = newReciters,
                    selectedReciterForDelete = null,
                    showDeleteConfirmationDialog = false,
                )
            }
        }

        private fun filterReciters(reciters: List<Reciter>): List<Reciter> {
            return if (tilawahArgs.surahId != null)
                filterRecitersForSurah(reciters, tilawahArgs.surahId)
            else
                reciters
        }

        private fun filterRecitersForSurah(reciters: List<Reciter>, surahId: Int?): List<Reciter> {
            val fakeDownloadedRecitersBySurahId = mapOf(
                1 to setOf(1, 2, 3),
                2 to setOf(2, 4),
                3 to setOf(1, 4, 5),
                18 to setOf(2, 5, 7),
                36 to setOf(3, 6, 9),
            )

            val allowedIds = fakeDownloadedRecitersBySurahId[surahId]
                ?: reciters.map { it.id }.toSet()
            return reciters.filter { it.id in allowedIds }
        }

        private suspend fun onDownloadComplete(reciterId: Int) {
            val surahId = surahArgs.surahId ?: return

            val isDownloaded = quranRepository.isSurahAudioCached(surahId, reciterId)

            updateState { currentState ->
                currentState.copy(
                    reciters = currentState.reciters.map { reciter ->
                        if (reciter.id == reciterId) {
                            reciter.copy(isDownloaded = isDownloaded)
                        } else {
                            reciter
                        }
                    }
                )
            }
        }
    }
}
