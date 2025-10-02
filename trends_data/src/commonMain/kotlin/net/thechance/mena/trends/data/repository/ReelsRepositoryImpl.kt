package net.thechance.mena.trends.data.repository

import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.io.buffered
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.dto.UpdateReelRequestDTO
import net.thechance.mena.trends.data.dto.UploadReelResponse
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.data.util.NetworkConstants.PAGE_PARAMETER
import net.thechance.mena.trends.data.util.NetworkConstants.REELS_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS_PATH
import net.thechance.mena.trends.data.util.infiniteTimeOut
import net.thechance.mena.trends.data.util.observeUploading
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [ReelsRepository::class])
internal class ReelsRepositoryImpl(
    @Provided private val networkClient: NetworkClient
) : ReelsRepository {

    override suspend fun deleteReelById(id: String) {
        safeApiCall<Unit> {
            networkClient.delete("$TRENDS_PATH/$REELS_ENDPOINT/$id")
        }
    }

    override suspend fun getAllReels(pageNumber: Int): List<Reel> {
        return safeApiCall<RemotePaginationResponse<ReelDto>> {
            networkClient.get("$TRENDS_PATH/$REELS_ENDPOINT") {
                parameter(PAGE_PARAMETER, pageNumber)
            }
        }.results?.map { it.toEntity() } ?: emptyList()
    }

    override suspend fun updateReelById(
        id: String,
        description: String,
        categoryIds: List<String>
    ) {
        val request = UpdateReelRequestDTO(description, categoryIds)
        safeApiCall<Unit> {
            networkClient.put("$TRENDS_PATH/$REELS_ENDPOINT/$id") {
                setBody(request)
            }
        }
    }

    override fun uploadReel(
        name: String,
        mimeType: String,
        size: Long,
        bytes: ByteArray
    ): Flow<UploadReelProgress> {
        return channelFlow {
            val response = safeApiCall<UploadReelResponse> {
                networkClient.post(urlString = "$TRENDS_PATH/$REELS_ENDPOINT") {
                    infiniteTimeOut()
                    setBody(createUploadReelBody(name, bytes, size, mimeType))
                    observeUploading { sent, total ->
                        send(
                            UploadReelProgress(
                                numberOfUploadedBytes = sent,
                                totalBytes = total
                            )
                        )
                    }
                }
            }
            send(
                UploadReelProgress(
                    reelId = response.reelId.orEmpty(),
                    numberOfUploadedBytes = size,
                    totalBytes = size
                )
            )
        }
    }

    private fun createUploadReelBody(
        name: String,
        reelBytes: ByteArray,
        size: Long,
        mimeType: String
    ): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            formData {
                append(
                    key = "video",
                    value = InputProvider(size) {
                        ByteReadChannel(reelBytes).asSource().buffered()
                    },
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "video/*")
                        append(HttpHeaders.ContentDisposition, "filename=\"$name.$mimeType\"")
                    }
                )
            }
        )
    }
}