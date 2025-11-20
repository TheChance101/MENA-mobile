package net.thechance.mena.faith.presentation.feature.quran.surah

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.model.Reciter

data class SurahUiState(
    val ayatOfSurah: List<Ayah> = emptyList(),
    val isAyahActionButtonsVisible: Boolean = false,
    val isPlayerVisible: Boolean = false,
    val isAyahSoundPlaying: Boolean = false,
    val currentPlayingAyahNumber: Int? = null,
    val currentPlayingAyahUrl: String? = null,
    val currentReciter: ReciterUiState = ReciterUiState(),
    val surahId: Int = 0,
    val surahName: String = "",
    val selectedAyah: String = "",
    val selectedAyahNumber: Int? = null,
    val initialAyahToScroll: Int? = null,
    val lastVisibleAyahNumber: Int = 0,
    val isLoading: Boolean = false,
    val isBasmalaVisible: Boolean = false,
    val isAutoPlayEnabled: Boolean = false
)

data class ReciterUiState(
    val id: Int = 0,
    val name: String = "Mishari Rashid Alafasy",
)

fun Reciter.toUiState() = ReciterUiState(
    id = id,
    name = name
)