package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class SurahViewModel(
    private val quranRepository: QuranRepository,
    surahId: Int,
    surahName: String
): BaseViewModel<SurahScreenState, SurahScreenEffect>(
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
                updateState {
                    it.copy(
                        ayatOfSurah = ayat.map { ayah -> ayah.toUiState() }
                    )
                }
            },
            onFinally = { updateState { it.copy(isLoading = false) }
            }
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
                selectedAyahIndex = -1
            )
        }
    }

    override fun onBackClick() = sendEffect(SurahScreenEffect.NavigateBack)

    override fun onTextLayoutChanged(textLayoutResult: TextLayoutResult) {
        updateState {
            it.copy(ayahLayout = textLayoutResult)
        }
    }

    override fun onBookmarkClick(ayahNumber: Int) {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyahIndex = -1
            )
        }
    }

    override fun onCopyClick(ayahContent: String) {
        updateState {
            it.copy(
                isSnackBarVisible = true,
                selectedAyah = ayahContent,
                isAyahActionButtonsVisible = false,
                selectedAyahIndex = -1,
            )
        }
        viewModelScope.launch {
            delay(1500)
            updateState {
                it.copy(
                    isSnackBarVisible = false
                )
            }
        }
    }

    override fun onShareClick(ayahContent: String) {
        updateState {
            it.copy(
                isAyahActionButtonsVisible = false,
                selectedAyah = ayahContent,
                selectedAyahIndex = -1
            )
        }
        sendEffect(SurahScreenEffect.ShareAyah(ayahContent))
    }
}