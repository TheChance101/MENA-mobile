package net.thechance.mena.faith.presentation.feature.quran.surah

sealed class SurahScreenEffect {
    object NavigateBack : SurahScreenEffect()
    data class ShareAyah(val ayah: String) : SurahScreenEffect()
    data class NavigateToSearchScreen(val surahId: Int, val surahName: String) : SurahScreenEffect()
}
