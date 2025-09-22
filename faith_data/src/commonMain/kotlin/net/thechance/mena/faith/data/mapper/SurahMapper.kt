package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.domain.entity.Surah

fun SurahDto.toSurah(): Surah {
    val surahOrder = Surah.SurahOrder.entries.first { it.order == order }
    return Surah(
        id = order,
        order = surahOrder,
        name = name,
        ayahCount = ayahCount,
        isMakkia = surahOrder.isMakkia
    )
}