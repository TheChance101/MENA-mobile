package net.thechance.mena.admin_panel.data.mapper.dukan

import net.thechance.mena.admin_panel.data.mapper.orZero
import net.thechance.mena.admin_panel.data.remote.dto.dukan.CategoryDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ColorDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDto
import net.thechance.mena.admin_panel.domain.entity.dukan.Category
import net.thechance.mena.admin_panel.domain.entity.dukan.Color
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan

fun DukanDto.toEntity() = Dukan(
    id = id,
    name = name.orEmpty(),
    address = address.orEmpty(),
    imageUrl = imageUrl.orEmpty(),
    latitude = latitude.orZero(),
    longitude = longitude.orZero(),
    color = color?.toEntity(),
    style = style ?: Dukan.Style.NO_IMAGE,
    categories = categories?.map { it.toEntity() } ?: emptyList(),
)

fun ColorDto.toEntity() = Color(
    id = id,
    hexCode = hexCode.orEmpty()
)

fun CategoryDto.toEntity() = Category(
    id = id,
    icon = icon.orEmpty(),
    title = title.orEmpty()
)