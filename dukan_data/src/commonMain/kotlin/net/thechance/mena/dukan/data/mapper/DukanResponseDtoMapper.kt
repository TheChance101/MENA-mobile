package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.dukan.DukanResponseDto
import net.thechance.mena.dukan.domain.entity.DukanPreview


fun DukanResponseDto.toDomainPreview(): DukanPreview {
    return DukanPreview(
        id = id,
        name = name,
        imageUrl = imageUrl,
    )
}