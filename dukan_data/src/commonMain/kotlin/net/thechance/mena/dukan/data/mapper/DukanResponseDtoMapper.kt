package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.dukan.DukanResponseDto
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
fun DukanResponseDto.toEntity(): Dukan {
    return Dukan(
        id = id,
        name = name,
        isFavorite = isFavorite,
        imageUrl = imageUrl,
        address = "",
        coordinates = Dukan.Coordinates(
            latitude = 0.0,
            longitude = 0.0
        ),
        categories = emptySet(),
        status = Dukan.Status.PENDING,
        color = Color(
            id = Uuid.random(),
            hexCode = ""
        ),
        style = Dukan.Style.WIDE_IMAGE,
    )
}