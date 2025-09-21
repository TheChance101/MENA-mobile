package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah

fun AyahDto.toAyah() = Ayah(
    surahId = id,
    number = number,
    displayContent = text,
    plainTextContent = ayaTextEmlaey
)

fun SurahDto.toSurah(): Surah {
    val surahOrder = Surah.SurahOrder.entries[order - 1]
    return Surah(
        id = order,
        order = surahOrder,
        name = nameEn,
        ayahCount = ayahCount,
        isMakkia = surahOrder.isMakkia
    )
}