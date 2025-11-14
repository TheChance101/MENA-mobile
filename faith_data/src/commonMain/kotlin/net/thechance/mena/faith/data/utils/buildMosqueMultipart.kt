package net.thechance.mena.faith.data.utils

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
fun buildMosqueMultipart(
    name: String,
    address: String,
    latitude: Double,
    longitude: Double,
    image: ByteArray?,
): MultiPartFormDataContent {

    return MultiPartFormDataContent(
        formData {
            append("name", name)
            append("address", address)
            append("latitude", latitude.toString())
            append("longitude", longitude.toString())

            image?.let { bytes ->
                append(
                    key = "image",
                    value = buildPacket { writeFully(bytes) },
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                        append(
                            HttpHeaders.ContentDisposition,
                            "form-data; name=\"image\"; filename=\"${Uuid.random()}.jpg\""
                        )
                    }
                )
            }
        }
    )
}