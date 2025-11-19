package net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.service.DownloadSurahManager
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.args.SurahRecitersArgs

class SurahRecitersViewModel(
    private val quranRepository: QuranRepository,
    private val surahArgs: SurahRecitersArgs,
    private val downloadManager: DownloadSurahManager,
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
        updateState { state ->
            val filtered = if (query.isBlank()) state.allReciters
            else state.allReciters.filter { it.name.contains(query, ignoreCase = true) }

            state.copy(
                query = query,
                reciters = filtered
            )
        }
    }

    override fun onClearQueryClick() {
        updateState { state ->
            state.copy(
                query = "",
                reciters = state.allReciters
            )
        }
    }

    override fun onBackClick() =
        sendEffect(SurahRecitersScreenEffect.NavigateBack)

    override fun onDownloadClick(reciterId: Int) {
        tryToExecute(
            execute = {
                val surahId = surahArgs.surahId ?: return@tryToExecute
                downloadAndCacheSurah(surahId=surahId,reciterId= reciterId)
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
        updateState { state ->
            state.copy(selectedReciterId = reciterId)
        }
    }

    private suspend fun getAllRecitersSuccessfully(reciters: List<Reciter>) {
        val surahId = surahArgs.surahId ?: return

        val recitersUi = reciters.map { reciter ->
            reciter.toUi(
                isDownloaded = quranRepository.isSurahAudioCached(
                    surahId= surahId,
                    reciterId =reciter.id
                )
            )
        }

        updateState { state ->
            state.copy(
                allReciters = recitersUi,
                reciters = recitersUi
            )
        }
    }


    private suspend fun onDownloadComplete(reciterId: Int) {
        val surahId = surahArgs.surahId ?: return

        val isDownloaded = quranRepository.isSurahAudioCached(surahId, reciterId)

        updateState { state ->
            state.copy(
                reciters = state.reciters.map {
                    if (it.id == reciterId) it.copy(isDownloaded = isDownloaded)
                    else it
                },
                allReciters = state.allReciters.map {
                    if (it.id == reciterId) it.copy(isDownloaded = isDownloaded)
                    else it
                }
            )
        }
    }
}
