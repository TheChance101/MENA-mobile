package net.thechance.mena.dukan.data.repository

import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.dukan.DukanResponseDto
import net.thechance.mena.dukan.data.dto.dukan.TopDiscountedDukanDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.mapper.toEntity
import net.thechance.mena.dukan.data.util.constants.EndPoints.DUKAN_BASE_PATH
import net.thechance.mena.dukan.data.util.network.DukanApi
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.TopDiscountedDukanPreview
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.identity.domain.service.LocationService

class DukanDiscoveryRepositoryImpl(
    private val client: DukanApi,
    private val locationService: LocationService,
    private val authorizationService: AuthorizationService,
) : DukanDiscoveryRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        observeAuthenticationState()
    }

    override suspend fun getEditorPicksDukans(
        page: Int,
        size: Int
    ): PagedResult<Dukan> {
        return safeApiCall<PageResponseDto<DukanResponseDto>> {
            client.getClient().get("$DUKAN_BASE_PATH/editor_picks") {
                parameter("page", page)
                parameter("size", size)
            }
        }.toDomain { it.toEntity() }
    }

    override suspend fun getBestAroundDukans(
        page: Int,
        size: Int
    ): PagedResult<Dukan> {
        val location = locationService.getActiveAddress()
        //TODO handle in backend
        val range = 30000
        return safeApiCall<PageResponseDto<DukanResponseDto>> {
            client.getClient().get("$DUKAN_BASE_PATH/nearby/best") {
                parameter("page", page)
                parameter("size", size)
                parameter("lat", location?.latitude)
                parameter("lng", location?.longitude)
                parameter("range", range)
            }
        }.toDomain { it.toEntity() }
    }

    override suspend fun getDukansByCategory(
        categoryId: String,
        page: Int,
        size: Int
    ): PagedResult<Dukan> {
        return safeApiCall<PageResponseDto<DukanResponseDto>> {
            client.getClient().get("$DUKAN_BASE_PATH/categories/$categoryId") {
                parameter("page", page)
                parameter("size", size)
            }
        }.toDomain(mapper = DukanResponseDto::toEntity)
    }

    override suspend fun getTopDiscountedDukans(
        page: Int,
        size: Int
    ): PagedResult<TopDiscountedDukanPreview> {
        return safeApiCall<PageResponseDto<TopDiscountedDukanDto>> {
            client.getClient().get("$DUKAN_BASE_PATH/top/discounts") {
                parameter("page", page)
                parameter("size", size)
            }
        }.toDomain(mapper = TopDiscountedDukanDto::toEntity)
    }

    private fun observeAuthenticationState() {
        repositoryScope.launch {
            authorizationService.observeAccessToken().collect { token ->
                if (token.isNotEmpty()) {
                    client.reset()
                }
            }
        }
    }
}