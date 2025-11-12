package net.thechance.mena.admin_panel.domain.repository.dukan

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.domain.model.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface DukanRepository {
    suspend fun getDukanDetails(dukanId: Uuid): Dukan
    suspend fun getDukanShelves(dukanId: Uuid, page: Int, size: Int): PagedResult<Shelf>
    suspend fun getShelfProducts(shelfId: Uuid, page: Int, size: Int): PagedResult<Product>
}