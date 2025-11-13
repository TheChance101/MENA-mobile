package net.thechance.mena.admin_panel.data.mapper.dukan

import net.thechance.mena.admin_panel.data.mapper.toUuidOrNull
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ShelfDto
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ShelfDto.toEntity() = Shelf(
    id = id.toUuidOrNull() ?: throw IllegalStateException("Invalid shelf id"),
    title = title.orEmpty()
)