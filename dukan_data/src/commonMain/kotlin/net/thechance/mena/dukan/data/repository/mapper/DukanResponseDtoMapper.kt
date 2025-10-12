package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.DukanResponseDto
import net.thechance.mena.dukan.domain.entity.DukanPreview


fun DukanResponseDto.toDomainPreview(): DukanPreview {
    return DukanPreview(
        id = id,
        name = name,
        imageUrl = imageUrl,
    )
}