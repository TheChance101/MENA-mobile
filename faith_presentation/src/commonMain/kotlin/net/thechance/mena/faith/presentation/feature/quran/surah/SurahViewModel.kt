package net.thechance.mena.faith.presentation.feature.quran.surah

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark_added_successfully
import mena.faith_presentation.generated.resources.copied_ayah_failed
import mena.faith_presentation.generated.resources.copied_ayah_successfully
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.SnackBarState
import net.thechance.mena.faith.presentation.feature.quran.surah.args.ISurahArgs
import net.thechance.mena.faith.presentation.util.ClipboardManager
import net.thechance.mena.faith.presentation.util.provider.ResourceProvider

class SurahViewModel(
    private val surahArgs: ISurahArgs,
    private val quranRepository: QuranRepository,
    private val clipboardManager: ClipboardManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bookmarkRepository: BookmarkRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<SurahScreenState, SurahScreenEffect>(
    initialState = SurahScreenState(surahId = surahArgs.surahId, surahName = surahArgs.surahName)
), SurahInteractionListener {

    init {
        loadSurahData(surahArgs.surahId)
    }

    private fun loadSurahData(surahId: Int) {
        tryToExecute(
            execute = { quranRepository.getAyatOfSurah(surahId) },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { ayat ->
                handleBasmalaVisibility(surahId = surahId)
                updateState {
                    it.copy(ayatOfSurah = ayat)
                }
            },
            onFinally = { updateState { it.copy(isLoading = false) } },
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

    override fun onSearchClick() {
        sendEffect(SurahScreenEffect.NavigateToSearchScreen(surahArgs.surahId, surahArgs.surahName))
    }

    override fun onCopyClick(ayahContent: String) {
        tryToExecuteSuspend(
            execute = { clipboardManager.copy(ayahContent) },
            onSuccess = { onCopySuccess(ayahContent) },
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
        tryToExecuteSuspend(
            execute = {
                bookmarkRepository.addAyahBookmark(
                    surahId = surahArgs.surahId,
                    ayahNumber = ayahNumber
                )
            },
            onSuccess = { onAddBookmarkSuccess() },
            dispatcher = dispatcher
        )
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahIndex = null
            )
        }
    }

    private suspend fun onAddBookmarkSuccess() {
        showSnackBar(
            message = resourceProvider.getString(Res.string.bookmark_added_successfully),
            status = SnackBarState.Status.Success
        )
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

    private suspend fun onCopySuccess(ayahContent: String) {
        showSuccessSnackBar()
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahIndex = null,
                selectedAyah = ayahContent
            )
        }
    }

    private suspend fun showSuccessSnackBar() {
        showSnackBar(
            message = resourceProvider.getString(Res.string.copied_ayah_successfully),
            status = SnackBarState.Status.Success,
        )
    }

    private suspend fun showErrorSnackBar() {
        showSnackBar(
            message = resourceProvider.getString(Res.string.copied_ayah_failed),
            status = SnackBarState.Status.Error,
        )
    }

    private fun handleBasmalaVisibility(surahId: Int) {
        val isTawbah = surahId == Surah.SurahOrder.AtTawbah.order
        val isFatiha = surahId == Surah.SurahOrder.AlFatihah.order

        val shouldShowBasmala = !(isTawbah || isFatiha)

        updateState {
            it.copy(isBasmalaVisible = shouldShowBasmala)
        }
    }
}
