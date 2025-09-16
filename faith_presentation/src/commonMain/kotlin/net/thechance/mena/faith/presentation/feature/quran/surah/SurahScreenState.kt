package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.compose.ui.text.TextLayoutResult

data class SurahScreenState(
    val ayatOfSurah: List<AyahUiState> = emptyList(),
    val isAyahActionButtonsVisible: Boolean = false,
    val surahId: Int = 0,
    val surahName: String = "",
    val isSnackBarVisible: Boolean = false,
    val selectedAyah: String = "",
    val selectedAyahIndex: Int = -1,
    val ayahLayout: TextLayoutResult? = null,
    val isLoading: Boolean = false
)

data class AyahUiState(
    val number: Int,
    val surahId: Int,
    val content: String
)