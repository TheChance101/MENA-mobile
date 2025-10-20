package net.thechance.mena.faith.presentation.feature.quran.sur

import net.thechance.mena.faith.domain.entity.Surah
import org.jetbrains.compose.resources.DrawableResource

data class SurUiState(
    val sur: List<SurahUiState> = emptyList(),
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

fun Surah.toUi() = SurUiState.SurahUiState(
    id = id,
    surahOrder = order.order,
    arabicNameImg = getSurahNameDrawableResource(order),
    surahName = name,
    ayatCount = ayahCount,
    isMakki = order.isMakkia
)
