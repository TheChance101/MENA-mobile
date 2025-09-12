package net.thechance.mena.faith.presentation.feature.quran.sur

import org.jetbrains.compose.resources.DrawableResource

data class SurUiState(
    val sur: List<SurahUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    data class SurahUi(
        val id: Int,
        val surahOrder: Int,
        val arabicNameImg: DrawableResource,
        val surahName: String,
        val ayatNumber: Int,
        val isMakki: Boolean,
    )
}
