package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.identity.domain.util.AppLanguage

fun SurahDto.toSurah(language: AppLanguage): Surah {
    val surahOrder = Surah.SurahOrder.entries.first { it.order == number }
    return Surah(
        id = number,
        order = surahOrder,
        name = if (language == AppLanguage.ARABIC) nameAr else nameEn,
        ayahCount = ayahCount ?: 0
    )
}