package net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.service.DownloadSurahManager
import net.thechance.mena.faith.domain.usecase.SearchRecitersUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.args.SurahRecitersArgs

class SurahRecitersViewModel(
    private val quranRepository: QuranRepository,
    private val surahArgs: SurahRecitersArgs,
    private val downloadManager: DownloadSurahManager,
    private val quranPlayer: QuranPlayer,
    private val searchRecitersUseCase: SearchRecitersUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<SurahRecitersUiState, SurahRecitersScreenEffect>(
    initialState = SurahRecitersUiState(surahId = surahArgs.surahId),
), SurahRecitersListener {

    init {
        getAllReciters()
        updateDefaultReciter()
    }

    private fun updateDefaultReciter() {
        tryToExecute(
            execute = { quranRepository.getDefaultReciter() },
            onSuccess = { reciterId -> updateSelectedReciter(reciterId.first()) },
        )
    }

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        getAllReciters()
    }

    override fun onClearQueryClick() {
        updateState { it.copy(query = "") }
        getAllReciters()
    }

    override fun playReciterSample(reciterId: Int) {
        tryToExecute(
            execute = {
                quranRepository.getAyahSoundUrl(
                    ayahNumber = 1,
                    surahNumber = 1,
                    reciterId = reciterId
                )
            },
            onSuccess = { quranPlayer.playAyah(it) },
            dispatcher = Main
        )
    }


    override fun onBackClick() =
        sendEffect(SurahRecitersScreenEffect.NavigateBack)

    override fun onDownloadClick(reciterId: Int) {
        tryToExecute(
            execute = {
                val surahId = surahArgs.surahId ?: return@tryToExecute
                downloadAndCacheSurah(surahId = surahId, reciterId = reciterId)
            },
            onSuccess = { onDownloadComplete(reciterId) },
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
        updateState { it.copy(selectedReciterId = reciterId) }
    }

    private suspend fun getAllRecitersSuccessfully(reciters: List<Reciter>) {
        val surahId = surahArgs.surahId ?: return
        val query = uiState.value.query

        val filteredReciters = searchRecitersUseCase(query, reciters)

        val recitersUi = filteredReciters.map { reciter ->
            reciter.toUi(
                isDownloaded = quranRepository.isSurahAudioCached(
                    surahId = surahId,
                    reciterId = reciter.id
                )
            )
        }

        updateState { it.copy(reciters = recitersUi) }
    }

    private suspend fun onDownloadComplete(reciterId: Int) {
        val surahId = surahArgs.surahId ?: return

        val isDownloaded = quranRepository.isSurahAudioCached(surahId, reciterId)

        updateState { state ->
            state.copy(
                reciters = state.reciters.map {
                    if (it.id == reciterId) it.copy(isDownloaded = isDownloaded)
                    else it
                }
            )
        }
    }
}