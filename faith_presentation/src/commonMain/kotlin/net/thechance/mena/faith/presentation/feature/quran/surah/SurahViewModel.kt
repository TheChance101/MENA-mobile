package net.thechance.mena.faith.presentation.feature.quran.surah

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.SnackBarState
import net.thechance.mena.faith.presentation.util.ClipboardManager

class SurahViewModel(
    surahId: Int,
    surahName: String,
    private val quranRepository: QuranRepository,
    private val clipboardManager: ClipboardManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<SurahScreenState, SurahScreenEffect>(
    initialState = SurahScreenState(surahId = surahId, surahName = surahName)
), SurahInteractionListener {

    init {
        loadSurahData(surahId)
    }

    private fun loadSurahData(surahId: Int) {
        tryToExecute(
            execute = { quranRepository.getAyatOfSurah(surahId) },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { ayat ->
                updateState { it.copy(ayatOfSurah = ayat) }
            },
            onFinally = { updateState { it.copy(isLoading = false) }
            },
            dispatcher = dispatcher
        )
    }

    override fun onAyahLongPress(ayahContent: String, ayahIndex: Int) {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = true,
                selectedAyah = ayahContent,
                selectedAyahIndex = ayahIndex,
            )
        }
    }

    override fun onCopyClick(ayahContent: String) {
        tryToExecute(
            execute = { clipboardManager.copy(ayahContent) },
            onSuccess = {
                showSuccessSnackBar()
                updateState {
                    it.copy(
                        isAyahActionButtonsVisible = false,
                        selectedAyahIndex = null,
                        selectedAyah = ayahContent
                    )
                }
            },
            onError = { showErrorSnackBar() },
            dispatcher = dispatcher
        )
    }

    override fun onDismissActionButtons() {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyah = "",
                selectedAyahIndex = null
            )
        }
    }

    override fun onBackClick() = sendEffect(SurahScreenEffect.NavigateBack)

    override fun onBookmarkClick(ayahNumber: Int) {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahIndex = null
            )
        }
    }

    override fun onShareClick(ayahContent: String) {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyah = ayahContent,
                selectedAyahIndex = null
            )
        }
        sendEffect(SurahScreenEffect.ShareAyah(ayahContent))
    }

    private fun showSuccessSnackBar() {
        showSnackBar(
            message = "Copied message successfully",
            status = SnackBarState.Status.Success,
        )
    }

    private fun showErrorSnackBar() {
        showSnackBar(
            message = "Copied message Failed",
            status = SnackBarState.Status.Error,
        )
    }
}