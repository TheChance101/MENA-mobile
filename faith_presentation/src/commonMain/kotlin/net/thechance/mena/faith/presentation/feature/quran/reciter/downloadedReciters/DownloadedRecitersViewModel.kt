package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.args.DownloadedRecitersArgs

class DownloadedRecitersViewModel(
    private val quranRepository: QuranRepository,
    private val surahArgs: DownloadedRecitersArgs,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DownloadedRecitersUiState, DownloadedRecitersEffect>(
    initialState = DownloadedRecitersUiState(
        surahId = surahArgs.surahId,
        isSwipeable = surahArgs.isSwipeToDeleteEnabled,
    ),
), DownloadedRecitersListener {

    init {
        getAllReciters()
        updateDefaultReciter()
    }

    override fun onBackClick() = sendEffect(DownloadedRecitersEffect.NavigateBack)

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        applyLocalSearch(query)
    }

    override fun onClearQueryClick() = updateState { it.copy(query = "", displayedReciters = it.cachedReciters) }


    private fun applyLocalSearch(query: String) {
        val source = uiState.value.cachedReciters

        val filtered =
            if (query.isBlank()) source
            else source.filter { it.name.contains(query, ignoreCase = true) }

        updateState { it.copy(displayedReciters = filtered) }
    }

    override fun onDeleteReciterAudioClick(reciterId: Int) {
        updateState {
            it.copy(
                isDeleteConfirmationDialogVisible = true,
                reciterIdToDelete = reciterId
            )
        }
    }

    override fun onConfirmDeleteReciterClick() {
        // TODO CONFIRM DELETE
    }

    override fun onDismissDeleteDialog() {
        updateState {
            it.copy(
                isDeleteConfirmationDialogVisible = false,
                reciterIdToDelete = null
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

        val downloadedReciters = reciters.filter { reciter ->
            quranRepository.isSurahAudioCached(surahId = surahId, reciterId = reciter.id)
        }

        val mapped = downloadedReciters.map { reciter ->
            reciter.toUi(isDownloaded = true)
        }

        updateState {
            it.copy(
                cachedReciters = mapped,
                displayedReciters = mapped
            )
        }
    }
}
