package net.thechance.mena.admin_panel.data.mapper

import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import kotlin.collections.orEmpty

fun <DTO, ENTITY> PagedResponse<DTO>.toEntityList(mapper: DTO.() -> ENTITY): List<ENTITY> =
    this.items?.map { it.mapper() }.orEmpty()