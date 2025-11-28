package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.data.mapper.mosque.toMosque
import net.thechance.mena.faith.data.remote.model.PageResponse
import net.thechance.mena.faith.data.remote.model.mosque.MosqueDto
import net.thechance.mena.faith.data.remote.service.MosqueApiService
import net.thechance.mena.faith.data.utils.buildMosqueMultipart
import net.thechance.mena.faith.data.utils.executeApiSafely
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository

class MosqueRepositoryImpl(
    private val mosqueApiService: MosqueApiService,
) : MosqueRepository {
    override suspend fun addMosque(mosque: Mosque, imageBytes: ByteArray) {
        executeApiSafely {
            val multipartBody = buildMosqueMultipart(
                name = mosque.name,
                address = mosque.address,
                latitude = mosque.coordinates.latitude,
                longitude = mosque.coordinates.longitude,
                image = imageBytes
            )
            mosqueApiService.createMosque(multipartBody)
        }
    }


    override suspend fun getNearbyMosques(
        latitude: Double,
        longitude: Double,
        radius: Double,
    ): List<Mosque> = executeApiSafely<List<MosqueDto>> {
        mosqueApiService.getNearbyMosques(latitude, longitude, radius)
    }.map { it.toMosque() }


    override suspend fun getMosquesByName(query: String, page: Int, size: Int): List<Mosque> {
        val response = executeApiSafely<PageResponse<MosqueDto>> {
            mosqueApiService.searchMosquesByName(query, page, size)
        }
        return response.items?.map { it.toMosque() } ?: emptyList()
    }

}
