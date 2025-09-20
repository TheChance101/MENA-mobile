package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.data.util.NetworkConstants.PAGE
import net.thechance.mena.trends.data.util.NetworkConstants.REELS
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [ReelsRepository::class])
internal class ReelsRepositoryImpl(
    @Provided private val httpClient: HttpClient
) : ReelsRepository {

    override suspend fun deleteReelById(id: String) {
        safeApiCall<Unit> {
            httpClient.delete("$TRENDS/$REELS/$id")
        }
    }

    override suspend fun getAllReels(pageNumber: Int): List<Reel> {
        return safeApiCall<RemotePaginationResponse<ReelDto>> {
            httpClient.get("$TRENDS/$REELS") {
                parameter(PAGE, pageNumber)
            }
        }.results?.mapNotNull { it.toEntity() } ?: emptyList()
    }
}