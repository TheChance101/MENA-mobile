package net.thechance.mena.faith.presentation.feature.quran.surah

import net.thechance.mena.faith.domain.entity.Ayah

fun Ayah.toAyahUiState(): SurahScreenState.AyahUiState {
    return SurahScreenState.AyahUiState(
        number = number,
        surahId = surahId,
        content = content
    )
}