package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.domain.entity.Ayah

fun AyahDto.toAyah() = Ayah(
    number = this.number,
    surahId = this.id,
    displayContent = text,
    plainTextContent = ayaTextEmlaey
)