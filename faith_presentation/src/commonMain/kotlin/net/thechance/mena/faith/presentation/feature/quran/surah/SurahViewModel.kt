package net.thechance.mena.faith.presentation.feature.quran.surah

import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class SurahViewModel(
    surahId: Int,
    surahName: String,
    private val repository: QuranRepository,
) : BaseViewModel<SurahScreenState, SurahScreenEffect>(
    initialState = SurahScreenState(surahId = surahId, surahName = surahName)
), SurahInteractionListener {

    init {
            loadSurahData(surahId)
    }

    private fun loadSurahData(surahId: Int) {
        tryToExecute(
            execute = { repository.getAyatOfSurah(surahId) },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { ayat ->
                updateState { it.copy(ayatOfSurah = ayat) }
            },
            onFinally = { updateState { it.copy(isLoading = false) } }
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
}