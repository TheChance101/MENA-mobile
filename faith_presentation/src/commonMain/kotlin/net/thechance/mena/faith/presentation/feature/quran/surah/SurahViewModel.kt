package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark_added_successfully
import mena.faith_presentation.generated.resources.copied_ayah_failed
import mena.faith_presentation.generated.resources.copied_ayah_successfully
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.ErrorState
import net.thechance.mena.faith.presentation.feature.quran.surah.args.SurahArgs
import net.thechance.mena.faith.presentation.utils.ClipboardManager

class SurahViewModel(
    private val surahArgs: SurahArgs,
    private val quranRepository: QuranRepository,
    private val clipboardManager: ClipboardManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bookmarkRepository: BookmarkRepository,
    private val quranPlayer: QuranPlayer,
) : BaseViewModel<SurahUiState, SurahScreenEffect>(
    initialState = SurahUiState(surahId = surahArgs.surahId),
), SurahInteractionListener {

    init {
        loadSurahData(surahArgs.surahId)
        observeDefaultReciter()
    }

    private fun loadSurahData(surahId: Int) {
        tryToExecute(
            execute = { quranRepository.getAyatOfSurah(surahId) },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { ayat -> handleLoadSurahSuccess(ayat) },
            onFinally = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher
        )
    }

    private fun observeDefaultReciter() {
        tryToCollect(
            onEmitNewValue = ::updateDefaultReciter,
            block = { quranRepository.getDefaultReciter() },
        )
    }

    private fun updateDefaultReciter(reciterId: Int) {
        tryToExecute(
            execute = { quranRepository.getReciterById(reciterId) },
            onSuccess = ::updateReciterState
        )
    }

    private fun updateReciterState(reciter: Reciter) =
        updateState { it.copy(currentReciter = reciter.toUiState()) }

    override fun onConfigrationChange() {
        updateState {
            it.copy(
                initialAyahToScroll = uiState.value.lastVisibleAyahNumber,
                selectedAyahNumber = null
            )
        }
    }

    override fun highlightAyah(ayahNumber: Int) {
        updateState {
            it.copy(
                initialAyahToScroll = ayahNumber,
                selectedAyahNumber = ayahNumber
            )
        }
    }

    override fun updateContinueTilawah(ayahNumber: Int) {
        tryToExecute(
            execute = {
                val lastAyah = LastAyahForTilawah(
                    surahId = surahArgs.surahId,
                    number = ayahNumber
                )
                quranRepository.saveLastAyahForTilawah(lastAyah)
                lastAyah
            },
            dispatcher = dispatcher,
            onSuccess = {
                updateState {
                    it.copy(
                        lastVisibleAyahNumber = ayahNumber,
                    )
                }
            }
        )
    }

    override fun playSurah(surahNumber: Int) {
        updateState {
            it.copy(
                selectedAyahNumber = uiState.value.selectedAyahNumber,
                isAutoPlayEnabled = true
            )
        }
        loadAndPlaySurah(surahNumber = surahNumber)
    }

    override fun onInitialAyahScrolled() {
        if (uiState.value.isAyahSoundPlaying || uiState.value.isPlayerVisible) return
        viewModelScope.launch {
            delay(2000L)
            updateState { it.copy(selectedAyahNumber = null, initialAyahToScroll = null) }
        }
    }

    override fun onAyahLongPress(ayahContent: String, ayahIndex: Int) {
        updateState {
            it.copy(
                isPlayerVisible = false,
                isAyahActionButtonsVisible = true,
                selectedAyah = ayahContent,
                selectedAyahNumber = ayahIndex
            )
        }
    }

    override fun onSearchClick() {
        sendEffect(SurahScreenEffect.NavigateToSearchScreen(surahArgs.surahId))
    }

    override fun onListenClick() {
        updateState { it.copy(isAutoPlayEnabled = false) }
        playAyah(uiState.value.selectedAyahNumber ?: 1)
    }

    override fun onReciterClick(surahId: Int) =
        sendEffect(SurahScreenEffect.NavigateToDownloadedRecitersScreen(surahArgs.surahId))

    override fun onNextAyahClick() = moveToAyah(offset = 1)

    override fun onPreviousAyahClick() = moveToAyah(offset = -1)

    override fun onPlayPauseClick() = togglePlayPause()

    override fun onRepeatAyahClick() {
        quranPlayer.repeatCurrentAyah()
        updateState { it.copy(isAyahSoundPlaying = true) }
    }

    override fun onClosePlayerClick() {
        quranPlayer.pauseAyah()
        updateState { it.copy(isAutoPlayEnabled = false) }
        onDismissActionButtons()
    }

    override fun onCopyClick(ayahContent: String) {
        tryToExecute(
            execute = { clipboardManager.copy(ayahContent) },
            onSuccess = { handleCopySuccess(ayahContent) },
            onError = { showErrorSnackBar() },
            dispatcher = dispatcher
        )
    }

    override fun onDismissActionButtons() {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyah = "",
                selectedAyahNumber = null,
                isPlayerVisible = false
            )
        }
    }

    override fun onBackClick() = sendEffect(SurahScreenEffect.NavigateBack)

    override fun onBookmarkClick(ayahNumber: Int) {
        tryToExecute(
            execute = {
                bookmarkRepository.addAyahBookmark(
                    surahId = surahArgs.surahId,
                    ayahNumber = ayahNumber
                )
            },
            onSuccess = { handleSuccessSnackBar(Res.string.bookmark_added_successfully) },
            dispatcher = dispatcher
        )
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahNumber = null
            )
        }
    }

    override fun onShareClick(content: String) {
        val surahId: Int = uiState.value.surahId
        val ayahNumber: Int = uiState.value.selectedAyahNumber ?: 1
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyah = content,
                selectedAyahNumber = null
            )
        }
        sendEffect(SurahScreenEffect.ShareAyah(
            surahId = surahId.toString(),
            ayahNumber = ayahNumber,
            ayahContent = content,
        ))
    }

    private fun playAyah(ayahNumber: Int) {
        loadAndPlayAyahSound(
            surahNumber = surahArgs.surahId,
            ayahNumber = ayahNumber,
            reciterId = uiState.value.currentReciter.id,
        )
        updateState { it.copy(selectedAyahNumber = ayahNumber) }
    }

    private fun moveToAyah(offset: Int) {
        val current = uiState.value.selectedAyahNumber ?: 1
        val next = (current + offset).let {
            if ((it > uiState.value.ayatOfSurah.size) || (it < 1)) 1 else it
        }
        playAyah(next)
    }

    private fun togglePlayPause() {
        val currentUrl = uiState.value.currentPlayingAyahUrl ?: return
        val isPlaying = uiState.value.isAyahSoundPlaying

        if (isPlaying) quranPlayer.pauseAyah()
        else quranPlayer.playAyah(currentUrl)

        updateState { it.copy(isAyahSoundPlaying = !isPlaying) }
    }

    private fun loadAndPlayAyahSound(surahNumber: Int, ayahNumber: Int, reciterId: Int) {
        tryToExecute(
            execute = {
                quranRepository.getAyahSoundUrl(
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                    reciterId = reciterId
                )
            },
            onSuccess = ::onLoadAyahSoundSuccess,
            onFinally = {
                updateState {
                    it.copy(
                        selectedAyahNumber = ayahNumber,
                        initialAyahToScroll = ayahNumber
                    )
                }
            },
            dispatcher = Main,
        )
    }

    private fun loadAndPlaySurah(surahNumber: Int) {
        tryToExecute(
            execute = {
                quranRepository.getAyahSoundUrl(
                    surahNumber = surahNumber,
                    ayahNumber = uiState.value.selectedAyahNumber ?: 1,
                    reciterId = uiState.value.currentReciter.id
                )
            },
            onSuccess = ::onLoadSurahSoundSuccess,
            onFinally = {
                updateState {
                    it.copy(
                        selectedAyahNumber = uiState.value.selectedAyahNumber,
                        initialAyahToScroll = uiState.value.selectedAyahNumber
                    )
                }
            },
            dispatcher = Main,
        )
    }

    private fun onLoadAyahSoundSuccess(ayahSoundUrl: String) {
        updateState {
            it.copy(
                isAyahSoundPlaying = true,
                isAyahActionButtonsVisible = false,
                isPlayerVisible = true,
                currentPlayingAyahUrl = ayahSoundUrl,
                currentPlayingAyahNumber = it.selectedAyahNumber
            )
        }
        quranPlayer.playAyah(ayahSoundUrl)
        updatePlayPause()
    }

    private fun onLoadSurahSoundSuccess(ayahSoundUrl: String) {
        tryToExecute(
            execute = {
                updateState {
                    it.copy(
                        isAyahSoundPlaying = true,
                        isAyahActionButtonsVisible = false,
                        isPlayerVisible = true,
                        currentPlayingAyahUrl = ayahSoundUrl,
                        currentPlayingAyahNumber = it.selectedAyahNumber
                    )
                }
                quranPlayer.playAyah(ayahSoundUrl)
            },
            onSuccess = { updateSurahPlayback() },
            dispatcher = Main
        )
    }

    private fun handleLoadSurahSuccess(ayat: List<Ayah>) {
        handleBasmalaVisibility(surahArgs.surahId)

        updateState {
            it.copy(
                ayatOfSurah = ayat,
                initialAyahToScroll = surahArgs.ayahNumber,
                selectedAyahNumber = surahArgs.ayahNumber
            )
        }
        updateSurahName(surahArgs.surahId)
    }

    private fun updateSurahName(surahId: Int) {
        tryToExecute(
            execute = { quranRepository.getSurahById(surahId) },
            onSuccess = { surah -> updateState { it.copy(surahName = surah.name) } },
            dispatcher = dispatcher
        )
    }

    private fun handleCopySuccess(ayahContent: String) {
        handleSuccessSnackBar(Res.string.copied_ayah_successfully)
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahNumber = null,
                selectedAyah = ayahContent
            )
        }
    }

    private fun showErrorSnackBar() {
        handleErrorSnackBar(ErrorState(Res.string.copied_ayah_failed))
    }

    private fun handleBasmalaVisibility(surahId: Int) {
        val isTawbah = surahId == Surah.SurahOrder.AtTawbah.order
        val isFatiha = surahId == Surah.SurahOrder.AlFatihah.order
        val shouldShowBasmala = !(isTawbah || isFatiha)
        updateState { it.copy(isBasmalaVisible = shouldShowBasmala) }
    }

    private fun updatePlayPause() {
        quranPlayer.onAyahCompleted {
            updateState { it.copy(isAyahSoundPlaying = false) }
            if (uiState.value.isAutoPlayEnabled) playNextAyahAutomatically()
        }
    }

    private fun updateSurahPlayback() {
        quranPlayer.onAyahCompleted {
            updateState { it.copy(isAyahSoundPlaying = false) }
            playNextAyahInSurah()
        }
    }

    private fun playNextAyahAutomatically() {
        val currentAyahNumber = uiState.value.currentPlayingAyahNumber ?: return
        val totalAyat = uiState.value.ayatOfSurah.size

        if (currentAyahNumber < totalAyat) {
            val nextAyahNumber = currentAyahNumber + 1
            viewModelScope.launch(Main) {
                playAyah(nextAyahNumber)
            }
        } else resetPlayerState()
    }

    private fun playNextAyahInSurah() {
        val currentAyahNumber = uiState.value.currentPlayingAyahNumber ?: return
        val totalAyat = uiState.value.ayatOfSurah.size

        if (currentAyahNumber < totalAyat) {
            val nextAyahNumber = currentAyahNumber + 1
            updateState { it.copy(selectedAyahNumber = nextAyahNumber) }
            loadAndPlaySurah(surahNumber = surahArgs.surahId)
        } else resetPlayerState()
    }

    private fun resetPlayerState() {
        updateState {
            it.copy(
                isPlayerVisible = false,
                selectedAyahNumber = null,
                currentPlayingAyahNumber = null,
                isAyahSoundPlaying = false,
                isAutoPlayEnabled = false
            )
        }
    }
}