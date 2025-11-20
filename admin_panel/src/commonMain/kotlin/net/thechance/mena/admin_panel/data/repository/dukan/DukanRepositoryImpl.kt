package net.thechance.mena.admin_panel.data.repository.dukan

import net.thechance.mena.admin_panel.data.local.InMemoryDukanDataStore
import net.thechance.mena.admin_panel.data.mapper.dukan.buildSortQueries
import net.thechance.mena.admin_panel.data.mapper.dukan.toEntity
import net.thechance.mena.admin_panel.data.mapper.toEntityPagedResult
import net.thechance.mena.admin_panel.data.remote.api_service.DukanApiService
import net.thechance.mena.admin_panel.data.remote.dto.DukanPagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDeactivationDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ProductDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ShelfDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.UpdateDukanStatusRequestDto
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.domain.model.DukanQueryParams
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class DukanRepositoryImpl(
    private val dukanApiService: DukanApiService,
    private val inMemoryDukanDataStore: InMemoryDukanDataStore
) : DukanRepository {

    override suspend fun getDukans(dukanQueryParams: DukanQueryParams): PagedResult<Dukan> {
        val sortParam = buildSortQueries(
            property = dukanQueryParams.sortType,
            direction = dukanQueryParams.sortDirection
        )
        return executeApiSafely<DukanPagedResponse<DukanDto>> {
            dukanApiService.getDukans(
                status = dukanQueryParams.status.toString(),
                query = dukanQueryParams.searchInput,
                sort = sortParam,
                page = dukanQueryParams.page,
                size = dukanQueryParams.size,
            )
        }.toEntityPagedResult(DukanDto::toEntity)
    }

    override fun storeDukanDetails(dukan: Dukan) = inMemoryDukanDataStore.storeDukan(dukan)


    override fun clearDukanDetails() = inMemoryDukanDataStore.clear()

    override fun getDukanDetails(): Dukan = requireNotNull(inMemoryDukanDataStore.getDukan())

    override suspend fun getDukanShelves(
        dukanId: Uuid,
        page: Int,
        size: Int
    ): PagedResult<Shelf> {
        return executeApiSafely<DukanPagedResponse<ShelfDto>> {
            dukanApiService.getDukanShelves(
                dukanId = dukanId.toString(),
                page = page,
                size = size
            )
        }.toEntityPagedResult(ShelfDto::toEntity)
    }

    override suspend fun getShelfProducts(
        shelfId: Uuid,
        page: Int,
        size: Int
    ): PagedResult<Product> {
        return executeApiSafely<DukanPagedResponse<ProductDto>> {
            dukanApiService.getShelfProducts(
                shelfId = shelfId.toString(),
                page = page,
                size = size
            )
        }.toEntityPagedResult(ProductDto::toEntity)
    }

    override suspend fun activateDukan(dukanId: Uuid) {
        executeApiSafely<Unit> {
            dukanApiService.activateDukan(dukanId = dukanId.toString())
        }
    }

    override suspend fun deactivateDukan(dukanId: Uuid, deactivationReason: String) {
        executeApiSafely<Unit> {
            dukanApiService.deactivateDukan(
                dukanId = dukanId.toString(),
                deactivateReason = DukanDeactivationDto(deactivationReason = deactivationReason)
            )
        }
    }

    override suspend fun updateDukanStatus(
        dukanId: Uuid,
        status: Dukan.Status,
        message: String
    ) {
        executeApiSafely {
            dukanApiService.updateDukanStatus(
                dukanId = dukanId.toString(),
                request = UpdateDukanStatusRequestDto(
                    status = status.toString(),
                    message = message
                )
            )
        }
    }
}