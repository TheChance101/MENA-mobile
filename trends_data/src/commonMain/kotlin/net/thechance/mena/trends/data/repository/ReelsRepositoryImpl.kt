package net.thechance.mena.trends.data.repository

import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
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
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.dto.UpdateReelRequestDTO
import net.thechance.mena.trends.data.dto.UploadReelResponse
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.data.util.NetworkConstants.FEED_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.JPEG_EXTENSION
import net.thechance.mena.trends.data.util.NetworkConstants.PAGE_PARAMETER
import net.thechance.mena.trends.data.util.NetworkConstants.REELS_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.THUMBNAIL
import net.thechance.mena.trends.data.util.NetworkConstants.THUMBNAIL_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.THUMBNAIL_MIME_TYPE
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS_PATH
import net.thechance.mena.trends.data.util.NetworkConstants.USER
import net.thechance.mena.trends.data.util.NetworkConstants.VIDEO
import net.thechance.mena.trends.data.util.VideoFileHandler
import net.thechance.mena.trends.data.util.getMediaMimeType
import net.thechance.mena.trends.data.util.observeUploading
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.data.util.setUploadRequestTimeout
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [ReelsRepository::class])
internal class ReelsRepositoryImpl(
    @Provided private val networkClient: NetworkClient,
    @Provided private val videoFileHandler: VideoFileHandler
) : ReelsRepository {

    override suspend fun deleteReelById(id: String) {
        safeApiCall<Unit> {
            networkClient.delete("$TRENDS_PATH/$REELS_ENDPOINT/$id")
        }
    }

    override suspend fun getAllCurrentUserReels(pageNumber: Int): List<Reel> {
        return safeApiCall<RemotePaginationResponse<ReelDto>> {
            networkClient.get("$TRENDS_PATH/$USER/$REELS_ENDPOINT") {
                parameter(PAGE_PARAMETER, pageNumber)
            }
        }.results?.map { it.toEntity() }.orEmpty()
    }

    override suspend fun getFeedReels(page: Int, reelId: String?): List<Reel> {
        val endpoint = reelId?.let {
            "$TRENDS_PATH/$REELS_ENDPOINT/$FEED_ENDPOINT/$it"
        } ?: "$TRENDS_PATH/$REELS_ENDPOINT/$FEED_ENDPOINT"

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
            networkClient.put("$TRENDS_PATH/$REELS_ENDPOINT/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override fun uploadReel(
        filePath: String,
        fileName: String,
        size: Long
    ): Flow<UploadReelProgress> {
        return channelFlow {
            val response = getUploadReelResponse(filePath, fileName, size) { sent, total ->
                send(
                    UploadReelProgress(
                        numberOfUploadedBytes = sent,
                        totalBytes = total
                    )
                )
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

    private suspend fun getUploadReelResponse(
        filePath: String,
        fileName: String,
        size: Long,
        onProgress: suspend (sent: Long, total: Long) -> Unit
    ): UploadReelResponse {
        return safeApiCall<UploadReelResponse> {
            val fileSource = videoFileHandler.readFile(filePath)
            networkClient.post(urlString = "$TRENDS_PATH/$REELS_ENDPOINT") {
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

    override suspend fun getReelThumbnail(filePath: String, timeMs: Long): ByteArray? {
        return videoFileHandler.extractVideoFrame(filePath, timeMs)
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