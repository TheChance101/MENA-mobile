package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import net.thechance.mena.dukan.data.repository.dto.DukanCategoryDto
import net.thechance.mena.dukan.data.repository.dto.DukanColorDto
import net.thechance.mena.dukan.data.repository.dto.MyDukanStatusDto
import net.thechance.mena.dukan.data.repository.mapper.toCreateDukanRequest
import net.thechance.mena.dukan.data.repository.mapper.toEntity
import net.thechance.mena.dukan.data.repository.util.safeApiCall
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.MyDukanStatus
import net.thechance.mena.dukan.domain.repository.DukanRepository

class DukanRepositoryImpl(
    private val client: HttpClient
) : DukanRepository {
    override suspend fun createDukan(dukan: Dukan) {
        safeApiCall<Unit> {
            client.post(
                urlString = "$BASE_URL/create"
            ) {
                setBody(dukan.toCreateDukanRequest())
            }
        }
    }

    override suspend fun getDukanStyles(): List<Dukan.Style> {
        return safeApiCall<List<String>> {
            client.get(
                urlString = "$BASE_URL/styles"
            )
        }.map {
            Dukan.Style.valueOf(it)
        }
    }

    override suspend fun getCategories(): List<Category> {
        return safeApiCall<List<DukanCategoryDto>> {
            client.get("$BASE_URL/categories")
        }.toEntity()
    }

    override suspend fun getDukanColors(): List<Color> {
        return safeApiCall<List<DukanColorDto>> {
            client.get(
                urlString = "$BASE_URL/colors"
            )
        }.toEntity()
    }

    override suspend fun getMyDukanStatus(): MyDukanStatus? {
        return safeApiCall<MyDukanStatusDto> {
            client.get(
                urlString = "$BASE_URL/status"
            )
        }.toEntity()
    }

    override suspend fun uploadDukanImage(
        fileName: String, fileBytes: ByteArray
    ): String {
        return safeApiCall {
            client.post("$BASE_URL/image") {
                setBody(
                    buildMultiPartFormData(fileName, fileBytes)
                )
            }
        }
    }

    override suspend fun isDukanNameTaken(name: String): Boolean {
        return safeApiCall { client.get("$BASE_URL/available?name=$name").body() }
    }

    private fun buildMultiPartFormData(
        fileName: String, fileBytes: ByteArray
    ): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            formData {
                append(
                    key = "file",
                    value = fileBytes,
                    headers = Headers.build {
                        append( HttpHeaders.ContentType, "multipart/form-data")
                        append( HttpHeaders.ContentDisposition,"filename=\"$fileName\"")
                    }
                )
            })

    }

    companion object {
        private const val BASE_URL = "/dukan"
    }
}