package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.surah.args.SurahArgs
import net.thechance.mena.faith.presentation.utils.ClipboardManager

class SurahViewModel(
    private val surahArgs: SurahArgs,
    private val quranRepository: QuranRepository,
    private val clipboardManager: ClipboardManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bookmarkRepository: BookmarkRepository,
    private val quranPlayer: QuranPlayer,
    snackbarHandler: SnackbarHandler
) : BaseViewModel<SurahUiState, SurahScreenEffect>(
    initialState = SurahUiState(surahId = surahArgs.surahId, surahName = surahArgs.surahName),
    snackbarHandler = snackbarHandler
), SurahInteractionListener {

    init {
        observeDefaultReciter()
        loadSurahData(surahArgs.surahId)
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
                    surahName = surahArgs.surahName,
                    number = ayahNumber
                )
                quranRepository.saveLastAyahForTilawah(lastAyah)
                lastAyah
            },
            dispatcher = dispatcher
        )
    }

    override fun onInitialAyahScrolled() {
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
        sendEffect(SurahScreenEffect.NavigateToSearchScreen(surahArgs.surahId, surahArgs.surahName))
    }

    override fun onListenClick() = playAyah(uiState.value.selectedAyahNumber ?: 1)

    override fun onReciterClick() = sendEffect(SurahScreenEffect.NavigateToDownloadedRecitersScreen)

    override fun onNextAyahClick() = moveToAyah(offset = 1)

    override fun onPreviousAyahClick() = moveToAyah(offset = -1)

    override fun onPlayPauseClick() = togglePlayPause()

    override fun onRepeatAyahClick() {
        quranPlayer.repeatCurrentAyah()
        updateState { it.copy(isAyahSoundPlaying = true) }
    }

    override fun onClosePlayerClick() {
        quranPlayer.pauseAyah()
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
            onSuccess = { handleAddBookmarkSuccess() },
            onError = { showErrorBookMarkSnackBar(it) },
            dispatcher = dispatcher
        )
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahNumber = null
            )
        }
    }

    override fun onShareClick(ayahContent: String) {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyah = ayahContent,
                selectedAyahNumber = null
            )
        }
        sendEffect(SurahScreenEffect.ShareAyah(ayahContent))
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
            dispatcher = Dispatchers.Main
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
    }

    private fun handleCopySuccess(ayahContent: String) {
        showCopySuccessSnackBar()
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahNumber = null,
                selectedAyah = ayahContent
            )
        }
    }

    private fun handleAddBookmarkSuccess() {
        snackbarHandler.showSnackBar(
            message = Res.string.bookmark_added_successfully,
            status = SnackBarState.Status.Success,
            scope = viewModelScope
        )
    }

    private fun showCopySuccessSnackBar() {
        snackbarHandler.showSnackBar(
            message = Res.string.copied_ayah_successfully,
            status = SnackBarState.Status.Success,
            scope = viewModelScope
        )
    }

    private fun showErrorSnackBar() {
        snackbarHandler.showSnackBar(
            message = Res.string.copied_ayah_failed,
            status = SnackBarState.Status.Error,
            scope = viewModelScope
        )
    }

    private fun showErrorBookMarkSnackBar(state: ErrorState) {
        snackbarHandler.showSnackBar(
            message = state.message,
            status = SnackBarState.Status.Error,
            scope = viewModelScope
        )
    }

    private fun handleBasmalaVisibility(surahId: Int) {
        val isTawbah = surahId == Surah.SurahOrder.AtTawbah.order
        val isFatiha = surahId == Surah.SurahOrder.AlFatihah.order
        val shouldShowBasmala = !(isTawbah || isFatiha)
        updateState { it.copy(isBasmalaVisible = shouldShowBasmala) }
    }
}
