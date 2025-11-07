@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.dukan.DukanResponseDto
import net.thechance.mena.dukan.domain.model.DukanPreview
import kotlin.uuid.ExperimentalUuidApi

fun DukanResponseDto.toPreviewEntity(): DukanPreview {
    return DukanPreview(
        id = id,
        name = name,
        imageUrl = imageUrl,
    )
}