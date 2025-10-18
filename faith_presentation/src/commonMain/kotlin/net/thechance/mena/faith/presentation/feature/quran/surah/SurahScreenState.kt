package net.thechance.mena.faith.presentation.feature.quran.surah

import net.thechance.mena.faith.domain.entity.Ayah

data class SurahScreenState(
    val ayatOfSurah: List<Ayah> = emptyList(),
    val isAyahActionButtonsVisible: Boolean = false,
    val surahId: Int = 0,
    val surahName: String = "",
    val selectedAyah: String = "",
    val selectedAyahIndex: Int? = null,
    val initialAyahToScroll: Int? = null,
    val isLoading: Boolean = false,
    val isBasmalaVisible: Boolean = false
)