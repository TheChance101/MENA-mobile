package net.thechance.mena.faith.presentation.feature.quran.tilwah

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.service.DownloadSurahManager
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.ErrorState
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.args.TilawahSurahArgs

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

    private suspend fun getAllRecitersSuccessfully(reciters: List<Reciter>) {
        val surahId = surahArgs.surahId ?: return

        val recitersUi = reciters.map { reciter ->
            reciter.toUi(
                isDownloaded = quranRepository.isSurahAudioCached(surahId, reciter.id)
            )
        }

        updateState { it.copy(reciters = recitersUi) }
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
