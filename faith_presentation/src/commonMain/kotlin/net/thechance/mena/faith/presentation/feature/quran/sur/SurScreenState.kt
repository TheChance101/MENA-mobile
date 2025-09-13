package net.thechance.mena.faith.presentation.feature.quran.sur

import org.jetbrains.compose.resources.DrawableResource

data class SurScreenState(
    val sur: List<SurahUiState> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    data class SurahUiState(
        val id: Int,
        val surahOrder: Int,
        val arabicNameImg: DrawableResource,
        val surahName: String,
        val ayatCount: Int,
        val isMakki: Boolean,
    )
}
