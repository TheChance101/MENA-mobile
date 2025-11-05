package net.thechance.mena.faith.presentation.feature.quran.surah

import net.thechance.mena.faith.domain.entity.Ayah

data class SurahUiState(
    val ayatOfSurah: List<Ayah> = emptyList(),
    val isAyahActionButtonsVisible: Boolean = false,
    val isPlayerVisible: Boolean = false,
    val isAyahSoundPlaying: Boolean = false,
    val currentPlayingAyahNumber: Int? = null,
    val currentPlayingAyahUrl: String? = null,
    val reciterName: String = "Mishari Rashid Alafasy",
    val surahId: Int = 0,
    val surahName: String = "",
    val selectedAyah: String = "",
    val selectedAyahNumber: Int? = null,
    val initialAyahToScroll: Int? = null,
    val isLoading: Boolean = false,
    val isBasmalaVisible: Boolean = false
)
