package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.io.buffered
import net.thechance.mena.trends.data.util.infiniteTimeOut
import net.thechance.mena.trends.data.util.observeUploading
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.UploadReelsRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [UploadReelsRepository::class])
class UploadReelsRepositoryImpl(
    @Provided private val httpClient: HttpClient
) : UploadReelsRepository {

    override fun uploadReel(
        name: String,
        mimeType: String,
        size: Long,
        bytes: ByteArray
    ): Flow<UploadReelProgress> {
        return channelFlow {
            safeApiCall<Unit> {  // TODO: return UploadVideoDto
                httpClient.post(urlString = "https://dlptest.com/https-post/") {  // TODO: change to real endpoint
                    infiniteTimeOut()
                    setBody(createUploadReelBody(name, bytes, size, mimeType))
                    observeUploading { sent, total ->
                        send(
                            UploadReelProgress(
                                reelId = "",
                                uploadedBytes = sent,
                                totalBytes = total
                            )
                        )
                    }
                }
            }
            send(
                UploadReelProgress(
                    reelId = "", // TODO: id of uploaded reel
                    uploadedBytes = size,
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
                    key = "video", // TODO:
                    value = InputProvider(size) { ByteReadChannel(reelBytes).asSource().buffered() },
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "video/*")
                        append(HttpHeaders.ContentDisposition, "filename=\"$name.$mimeType\"")
                    }
                )
            }
        )
    }
}