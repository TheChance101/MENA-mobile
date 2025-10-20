package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import net.thechance.mena.dukan.data.util.constants.EndPoints.DUKAN_BASE_PATH
import net.thechance.mena.dukan.data.util.constants.EndPoints.PRODUCT_BASE_PATH
import net.thechance.mena.dukan.data.util.network.buildMultiPartFormData
import net.thechance.mena.dukan.data.util.network.buildSinglePartFormData
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.repository.MediaRepository

class MediaRepositoryImpl(
    private val client: HttpClient
) : MediaRepository {
    override suspend fun uploadDukanImage(
        fileName: String, fileBytes: ByteArray
    ): String {
        return safeApiCall {
            client.post("$DUKAN_BASE_PATH/image") {
                setBody(
                    buildSinglePartFormData(fileName, fileBytes, "file")
                )
            }
        }
    }

    override suspend fun uploadProductImages(
        fileName: List<String>,
        fileBytes: List<ByteArray>,
        productId: String
    ): List<String> {
        require(fileName.size == fileBytes.size) {
            "fileNames and fileBytes must have the same size."
        }

        val parts: List<Pair<String, ByteArray>> = fileName.zip(fileBytes)

        return safeApiCall {
            client.post("${PRODUCT_BASE_PATH}/images/$productId") {
                accept(ContentType.Application.Json)
                setBody(buildMultiPartFormData(parts, fieldName = "files"))
            }
        }
    }
}