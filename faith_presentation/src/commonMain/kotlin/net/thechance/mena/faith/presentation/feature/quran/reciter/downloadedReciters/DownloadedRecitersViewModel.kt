package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.reciter_deleted_successfully_downloading
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.usecase.SearchRecitersUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.args.DownloadedRecitersArgs

class DownloadedRecitersViewModel(
    private val quranRepository: QuranRepository,
    private val surahArgs: DownloadedRecitersArgs,
    private val searchRecitersUseCase: SearchRecitersUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DownloadedRecitersUiState, DownloadedRecitersEffect>(
    initialState = DownloadedRecitersUiState(
        surahId = surahArgs.surahId),
), DownloadedRecitersListener {

    init {
        getAllReciters()
        updateDefaultReciter()
    }

    override fun onBackClick() = sendEffect(DownloadedRecitersEffect.NavigateBack)

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        getAllReciters()
    }

    override fun onClearQueryClick() {
        updateState { it.copy(query = "") }
        getAllReciters()
    }

    override fun onDeleteReciterAudioClick(reciterId: Int) {
        updateState {
            it.copy(
                isDeleteConfirmationDialogVisible = true,
            )
        }
        handleSuccessSnackBar(Res.string.reciter_deleted_successfully_downloading)
    }

    override fun onConfirmDeleteReciterClick() {
        // TODO CONFIRM DELETE
    }

    override fun onDismissDeleteDialog() {
        updateState {
            it.copy(
                isDeleteConfirmationDialogVisible = false,
            )
        }
    }

    override fun onSelectReciterClick(reciterId: Int) {
        tryToExecute(
            execute = { quranRepository.saveDefaultReciter(reciterId) },
            onSuccess = { updateSelectedReciter(reciterId) },
        )
    }

    private fun updateDefaultReciter() {
        tryToExecute(
            execute = { quranRepository.getDefaultReciter() },
            onSuccess = { id -> updateSelectedReciter(id.first()) },
        )
    }

    private fun updateSelectedReciter(reciterId: Int) {
        updateState { it.copy(selectedReciterId = reciterId) }
    }

    private fun getAllReciters() {
        tryToExecute(
            execute = { quranRepository.getReciters() },
            onSuccess = ::onGetAllRecitersSuccess,
            dispatcher = dispatcher
        )
    }

    private suspend fun onGetAllRecitersSuccess(reciters: List<Reciter>) {
        val surahId = surahArgs.surahId ?: return
        val query = uiState.value.query

        val downloadedReciters = reciters.filter { reciter ->
            quranRepository.isSurahAudioCached(surahId = surahId, reciterId = reciter.id)
        }

        val filteredReciters = searchRecitersUseCase(query, downloadedReciters)

        updateState {
            it.copy(
                reciters = filteredReciters.map { reciter -> reciter.toUi(isDownloaded = true) }
            )
        }
    }
}