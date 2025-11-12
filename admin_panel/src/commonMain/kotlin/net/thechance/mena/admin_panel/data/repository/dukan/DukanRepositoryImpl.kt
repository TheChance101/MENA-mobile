package net.thechance.mena.admin_panel.data.repository.dukan

import net.thechance.mena.admin_panel.data.mapper.dukan.toEntity
import net.thechance.mena.admin_panel.data.mapper.toEntityPagedResult
import net.thechance.mena.admin_panel.data.remote.api_service.DukanApiService
import net.thechance.mena.admin_panel.data.remote.dto.DukanPagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ProductDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ShelfDto
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class DukanRepositoryImpl(
    private val dukanApiService: DukanApiService,
) : DukanRepository {

    override suspend fun getDukanDetails(dukanId: Uuid): Dukan {
        return executeApiSafely<DukanDto> {
            dukanApiService.getDukanDetails(dukanId.toString())
        }.toEntity()
    }

    override suspend fun getDukanShelves(dukanId: Uuid, page: Int, size: Int): PagedResult<Shelf> {
        return executeApiSafely<DukanPagedResponse<ShelfDto>> {
            dukanApiService.getDukanShelves(dukanId = dukanId.toString(), page = page, size = size)
        }.toEntityPagedResult(ShelfDto::toEntity)
    }

    override suspend fun getShelfProducts(
        shelfId: Uuid,
        page: Int,
        size: Int
    ): PagedResult<Product> {
        return executeApiSafely<DukanPagedResponse<ProductDto>> {
            dukanApiService.getShelfProducts(shelfId = shelfId.toString(), page = page, size = size)
        }.toEntityPagedResult(ProductDto::toEntity)
    }
}