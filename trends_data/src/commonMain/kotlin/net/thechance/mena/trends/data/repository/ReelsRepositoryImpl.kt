package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.io.buffered
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.dto.UpdateReelRequestDTO
import net.thechance.mena.trends.data.dto.UploadReelResponse
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.data.util.NetworkConstants.JPEG_EXTENSION
import net.thechance.mena.trends.data.util.NetworkConstants.LIKE_REEL_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.PAGE_PARAMETER
import net.thechance.mena.trends.data.util.NetworkConstants.PROFILE_REELS_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.REELS_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.REELS_FEED_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.THUMBNAIL
import net.thechance.mena.trends.data.util.NetworkConstants.THUMBNAIL_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.THUMBNAIL_MIME_TYPE
import net.thechance.mena.trends.data.util.NetworkConstants.VIDEO
import net.thechance.mena.trends.data.util.NetworkConstants.VIEW_REEL_ENDPOINT
import net.thechance.mena.trends.data.util.VideoFileHandler
import net.thechance.mena.trends.data.util.getMediaMimeType
import net.thechance.mena.trends.data.util.observeUploading
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.data.util.setUploadRequestTimeout
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.model.UploadReelStatus
import net.thechance.mena.trends.domain.repository.ReelsRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [ReelsRepository::class])
internal class ReelsRepositoryImpl(
    @Provided private val networkClient: HttpClient,
    @Provided private val videoFileHandler: VideoFileHandler
) : ReelsRepository {

    override suspend fun deleteReelById(id: String) {
        safeApiCall<Unit> {
            networkClient.delete("$REELS_ENDPOINT/$id")
        }
    }

    override suspend fun getAllCurrentUserReels(pageNumber: Int): List<Reel> {
        return safeApiCall<RemotePaginationResponse<ReelDto>> {
            networkClient.get(PROFILE_REELS_ENDPOINT) {
                parameter(PAGE_PARAMETER, pageNumber)
            }
        }.results?.map { it.toEntity() }.orEmpty()
    }

    override suspend fun getFeedReels(page: Int, reelId: String?): List<Reel> {
        val endpoint = reelId?.let {
            "$REELS_FEED_ENDPOINT/$it"
        } ?: REELS_FEED_ENDPOINT

        return safeApiCall<RemotePaginationResponse<ReelDto>> {
            networkClient.get(endpoint) {
                parameter(PAGE_PARAMETER, page)
            }
        }.results.orEmpty().map { it.toEntity() }
    }

    override suspend fun updateReelById(
        id: String,
        description: String,
        categoryIds: List<String>
    ) {
        val request = UpdateReelRequestDTO(description, categoryIds)
        safeApiCall<Unit> {
            networkClient.put("$REELS_ENDPOINT/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override fun uploadReel(
        filePath: String,
        fileName: String,
        size: Long
    ): Flow<UploadReelStatus> {
        return channelFlow {
            val response = getUploadReelResponse(filePath, fileName, size) { sent, total ->
                send(
                    UploadReelStatus.UploadReelProgress(
                        numberOfUploadedBytes = sent,
                        totalBytes = total
                    )
                )
            }
            send(
                UploadReelStatus.UploadReelSuccess(
                    reelId = response.reelId.orEmpty(),
                )
            )
        }
    }

    private suspend fun getUploadReelResponse(
        filePath: String,
        fileName: String,
        size: Long,
        onProgress: suspend (sent: Long, total: Long) -> Unit
    ): UploadReelResponse {
        return safeApiCall<UploadReelResponse> {
            val fileSource = videoFileHandler.readFile(filePath)
            networkClient.post(urlString = REELS_ENDPOINT) {
                setUploadRequestTimeout()
                setBody(
                    createRequestBody(
                        fileName = fileName,
                        key = VIDEO,
                        mimeType = getMediaMimeType(fileName),
                        input = InputProvider(size) { fileSource.buffered() }
                    )
                )
                observeUploading(onProgress)
            }
        }
    }

    override suspend fun uploadReelThumbnail(
        reelId: String,
        fileName: String,
        thumbnail: ByteArray
    ) {
        safeApiCall<Unit> {
            networkClient.put(urlString = "$THUMBNAIL_ENDPOINT/${reelId}") {
                setBody(
                    createRequestBody(
                        fileName = "$reelId$JPEG_EXTENSION",
                        key = THUMBNAIL,
                        mimeType = THUMBNAIL_MIME_TYPE,
                        input = InputProvider { ByteReadChannel(thumbnail).asSource().buffered() }
                    )
                )
            }
        }
    }

    override suspend fun getReelDuration(filePath: String): Long? {
        return videoFileHandler.getDuration(filePath)
    }

    override suspend fun extractReelThumbnail(filePath: String, timeInMillis: Long): ByteArray? {
        return videoFileHandler.extractVideoFrame(filePath, timeInMillis)
    }

    override suspend fun toggleReelLike(reelId: String): Reel {
        return safeApiCall<ReelDto> {
            networkClient.post(urlString = "$LIKE_REEL_ENDPOINT/$reelId")
        }.toEntity()
    }

    override suspend fun addReelView(reelId: String) {
        safeApiCall<Unit> {
            networkClient.post(urlString = "$VIEW_REEL_ENDPOINT/$reelId")
        }
    }

    private fun createRequestBody(
        fileName: String,
        key: String,
        mimeType: String,
        input: InputProvider
    ): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            formData {
                append(
                    key = key,
                    value = input,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    }
                )
            }
        )
    }
}