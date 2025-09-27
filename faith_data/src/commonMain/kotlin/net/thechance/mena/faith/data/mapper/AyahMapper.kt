package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.domain.entity.Ayah

fun AyahDto.toAyah() = Ayah(
    number = number,
    surahId = id,
    displayContent = displayContent,
    plainTextContent = plainTextContent
)