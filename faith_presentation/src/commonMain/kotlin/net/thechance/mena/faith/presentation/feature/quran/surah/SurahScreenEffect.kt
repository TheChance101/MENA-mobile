package net.thechance.mena.faith.presentation.feature.quran.surah

sealed class SurahScreenEffect {
    object NavigateBack : SurahScreenEffect()
    data class ShareAyah(val ayah: String) : SurahScreenEffect()
    data class NavigateToDownloadedRecitersScreen(val surahId: Int) : SurahScreenEffect()
    data class NavigateToSearchScreen(val surahId: Int) : SurahScreenEffect()
}
