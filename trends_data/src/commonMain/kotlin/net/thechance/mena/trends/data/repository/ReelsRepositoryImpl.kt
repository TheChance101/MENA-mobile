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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.io.buffered
import net.thechance.mena.trends.data.di.TrendDataModule.Companion.DEFAULT_CLIENT_NAME
import net.thechance.mena.trends.data.di.TrendDataModule.Companion.UPLOAD_CLIENT_NAME
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.dto.UpdateReelRequestDTO
import net.thechance.mena.trends.data.dto.UploadReelResponse
import net.thechance.mena.trends.data.mapper.toEntity
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
import net.thechance.mena.trends.data.util.observeUploading
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.model.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import org.koin.core.annotation.Named
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [ReelsRepository::class])
internal class ReelsRepositoryImpl(
    @Named(DEFAULT_CLIENT_NAME) private val networkClient: HttpClient,
    @Named(UPLOAD_CLIENT_NAME) private val uploadClient: HttpClient,
    @Provided private val videoFileHandler: VideoFileHandler
) : ReelsRepository {

    private val observableUploadingFlow: MutableSharedFlow<UploadReelProgress> = MutableSharedFlow()

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

    override suspend fun uploadReel(filePath: String, size: Long): String {
        return safeApiCall<UploadReelResponse> {
            val fileSource = videoFileHandler.readFile(filePath)
            uploadClient.post(urlString = REELS_ENDPOINT) {
                setBody(
                    createRequestBody(
                        key = VIDEO,
                        mimeType = videoFileHandler.getMimeType(filePath),
                        input = InputProvider(size) { fileSource.buffered() }
                    )
                )
                observeUploading(observableUploadingFlow::emit)
            }
        }.reelId.orEmpty()
    }

    override fun observeUploadReelProgress(): SharedFlow<UploadReelProgress> {
        return observableUploadingFlow
    }

    override suspend fun uploadReelThumbnail(reelId: String, thumbnail: ByteArray) {
        safeApiCall<Unit> {
            uploadClient.put(urlString = "$THUMBNAIL_ENDPOINT/${reelId}") {
                setBody(
                    createRequestBody(
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
                        append(HttpHeaders.ContentDisposition, "filename=\"\"")
                    }
                )
            }
        )
    }
}