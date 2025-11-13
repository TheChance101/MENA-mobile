package net.thechance.mena.admin_panel.data.mapper.dukan

import net.thechance.mena.admin_panel.data.remote.dto.dukan.ShelfDto
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf

fun ShelfDto.toEntity() = Shelf(
    id = id,
    title = title.orEmpty()
)