package net.thechance.mena.faith.presentation.feature.quran.surah

data class SurahScreenState(
    val ayatOfSurah: List<AyahUiState> = emptyList(),
    val isAyahActionButtonsVisible: Boolean = false,
    val isSnackBarVisible: Boolean = false,
    val surahId: Int = 0,
    val surahName: String = "",
    val selectedAyah: String = "",
    val selectedAyahIndex: Int? = null,
    val isLoading: Boolean = false
) {
    data class AyahUiState(
        val number: Int,
        val content: String
    )
}