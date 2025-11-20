package net.thechance.mena.faith.data.utils

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun buildMosqueMultipart(
    name: String,
    address: String,
    latitude: Double,
    longitude: Double,
    image: ByteArray
): MultiPartFormDataContent {

    val mosqueJson = Json.encodeToString(
        MosqueRequest(
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            image = image
        )
    )

    return MultiPartFormDataContent(
        formData {
            append(
                key = "mosque",
                value = mosqueJson,
                headers = Headers.build {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.ContentDisposition, "form-data; name=\"mosque\"")
                }
            )
        }
    )
}

@Serializable
data class MosqueRequest(
    val name: String,
    val address: String,
    val  latitude: Double,
    val longitude: Double,
    val image: ByteArray,
)