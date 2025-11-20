package net.thechance.mena.admin_panel.domain.repository.dukan

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.domain.model.DukanQueryParams
import net.thechance.mena.admin_panel.domain.model.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface DukanRepository {
    suspend fun getDukans(dukanQueryParams: DukanQueryParams): PagedResult<Dukan>
    fun getDukanDetails(): Dukan
    fun storeDukanDetails(dukan: Dukan)
    fun clearDukanDetails()
    suspend fun getDukanShelves(dukanId: Uuid, page: Int, size: Int): PagedResult<Shelf>
    suspend fun getShelfProducts(shelfId: Uuid, page: Int, size: Int): PagedResult<Product>
    suspend fun activateDukan(dukanId: Uuid)
    suspend fun deactivateDukan(dukanId: Uuid, deactivationReason: String)
    suspend fun updateDukanStatus(dukanId: Uuid, status: Dukan.Status, message: String)
}