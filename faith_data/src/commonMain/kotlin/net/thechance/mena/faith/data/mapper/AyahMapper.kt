package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.domain.entity.Ayah

fun AyahDto.toAyah(): Ayah {
    return Ayah(
        number = number,
        surahId = surahNumber,
        content = content,
        plainContent = plainContent
    )
}