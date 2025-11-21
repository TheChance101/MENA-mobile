package net.thechance.mena.faith.data.utils

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun buildMosqueMultipart(
    name: String,
    address: String,
    latitude: Double,
    longitude: Double,
    image: ByteArray,
): MultiPartFormDataContent {

    return MultiPartFormDataContent(
        formData {

            append("name", name)
            append("address", address)
            append("latitude", latitude.toString())
            append("longitude", longitude.toString())
            appendInput(
                key = "image",
                headers = Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"${Uuid.random()}.jpg\"")
                    append(HttpHeaders.ContentType, "image/jpeg")
                }
            ) {
                buildPacket { writeFully(image) }
            }
        }
    )
}

