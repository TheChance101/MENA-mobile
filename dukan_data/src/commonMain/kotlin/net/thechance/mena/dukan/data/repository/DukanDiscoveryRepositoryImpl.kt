package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.dukan.DukanResponseDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.mapper.toDomainPreview
import net.thechance.mena.dukan.data.util.constants.EndPoints.DUKAN_BASE_PATH
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.DukanPreview
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.identity.domain.service.LocationService

class DukanDiscoveryRepositoryImpl(
    private val client: HttpClient,
    val locationService: LocationService
) : DukanDiscoveryRepository {
    override suspend fun getEditorPicksDukans(
        page: Int,
        size: Int
    ): PagedResult<DukanPreview> {
        val dukansResponse = safeApiCall<PageResponseDto<DukanResponseDto>> {
            client.get("$DUKAN_BASE_PATH/editor_picks") {
                parameter("page", page)
                parameter("size", size)
            }
        }
        return dukansResponse.toDomain { it.toDomainPreview() }
    }

    override suspend fun getBestAroundDukans(
        page: Int,
        size: Int
    ): PagedResult<DukanPreview> {
        val location = locationService.getUserAddresses().first { it.isActive }
        //TODO handle in backend
        val range = 30000
        val dukansResponse = safeApiCall<PageResponseDto<DukanResponseDto>> {
            client.get("$DUKAN_BASE_PATH/nearby/best") {
                parameter("page", page)
                parameter("size", size)
                parameter("lat", location.latitude)
                parameter("lng", location.longitude)
                parameter("range", range)
            }
        }
        return dukansResponse.toDomain { it.toDomainPreview() }
    }

    override suspend fun getDukansByCategory(
        categoryId: String,
        page: Int,
        size: Int
    ): PagedResult<DukanPreview> {
        return safeApiCall<PageResponseDto<DukanResponseDto>> {
            client.get("$DUKAN_BASE_PATH/categories/$categoryId") {
                parameter("page", page)
                parameter("size", size)
            }
        }.toDomain(mapper = DukanResponseDto::toDomainPreview)
    }
}