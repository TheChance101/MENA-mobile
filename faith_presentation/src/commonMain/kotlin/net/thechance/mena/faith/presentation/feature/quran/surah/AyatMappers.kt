package net.thechance.mena.faith.presentation.feature.quran.surah

import net.thechance.mena.faith.domain.entity.Ayah

fun Ayah.toUiState() = SurahScreenState.AyahUiState(
    number = number,
    content = content
)