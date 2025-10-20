package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.dukan.DukanResponseDto
import net.thechance.mena.dukan.domain.entity.DukanPreview
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
fun DukanResponseDto.toDomainPreview(): DukanPreview {
    return DukanPreview(
        id = id,
        name = name,
        imageUrl = imageUrl,
    )
}