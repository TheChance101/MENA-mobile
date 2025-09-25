package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.dto.UpdateReelRequestDTO
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.data.util.NetworkConstants.PAGE_PARAMETER
import net.thechance.mena.trends.data.util.NetworkConstants.REELS_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS_PATH
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [ReelsRepository::class])
class ReelsRepositoryImpl(
    @Provided private val httpClient: HttpClient
) : ReelsRepository {

    override suspend fun deleteReelById(id: String) {
        safeApiCall<Unit> {
            httpClient.delete("$TRENDS_PATH/$REELS_ENDPOINT/$id")
        }
    }

    override suspend fun getAllReels(pageNumber: Int): List<Reel> {
        return safeApiCall<RemotePaginationResponse<ReelDto>> {
            httpClient.get("$TRENDS_PATH/$REELS_ENDPOINT") {
                parameter(PAGE_PARAMETER, pageNumber)
            }
        }.results?.mapNotNull { it.toEntity() } ?: emptyList()
    }

    override suspend fun updateReelById(
        id: String,
        description: String,
        categoryIds: List<String>
    ): Reel {
        val request = UpdateReelRequestDTO(description, categoryIds)
        return safeApiCall<ReelDto> {
            httpClient.put("$TRENDS_PATH/$REELS_ENDPOINT/$id") {
                contentType(ContentType.Application.Json)//TODO dont know if this part is necessary
                setBody(request)
            }
        }.toEntity() ?: throw Exception("Update failed")//TODO dont know if it okey if i throw exception and what it will replace it
    }
}