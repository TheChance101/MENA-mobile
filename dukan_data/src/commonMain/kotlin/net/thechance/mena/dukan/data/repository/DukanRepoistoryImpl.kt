package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.repository.dto.DukanCategoryResponse
import net.thechance.mena.dukan.data.repository.dto.DukanColorsResponse
import net.thechance.mena.dukan.data.repository.dto.DukanDto
import net.thechance.mena.dukan.data.repository.dto.DukanNameResponse
import net.thechance.mena.dukan.data.repository.dto.MyDukanStatusDto
import net.thechance.mena.dukan.data.repository.dto.PageResponseDto
import net.thechance.mena.dukan.data.repository.mapper.toCategoryList
import net.thechance.mena.dukan.data.repository.mapper.toColorsList
import net.thechance.mena.dukan.data.repository.mapper.toCreateDukanRequest
import net.thechance.mena.dukan.data.repository.mapper.toDomain
import net.thechance.mena.dukan.data.repository.mapper.toMyDukanStatus
import net.thechance.mena.dukan.data.repository.util.buildSinglePartFormData
import net.thechance.mena.dukan.data.repository.util.safeApiCall
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.MyDukanStatus
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.util.PagedResult

class DukanRepositoryImpl(
    private val client: HttpClient
) : DukanRepository {
    override suspend fun createDukan(dukan: Dukan) {
        safeApiCall<Unit> {
            client.post(
                urlString = "$BASE_URL/create"
            ) {
                contentType(ContentType.Application.Json)
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
        return safeApiCall<DukanCategoryResponse> {
            client.get("$BASE_URL/categories")
        }.categories.toCategoryList()
    }

    override suspend fun getDukanColors(): List<Color> {
        return safeApiCall<DukanColorsResponse> {
            client.get(
                urlString = "$BASE_URL/colors"
            )
        }.colors.toColorsList()
    }

    override suspend fun getMyDukanStatus(): MyDukanStatus? {
        return safeApiCall<MyDukanStatusDto> {
            client.get(
                urlString = "$BASE_URL/statues"
            )
        }.toMyDukanStatus()
    }

    override suspend fun uploadDukanImage(
        fileName: String, fileBytes: ByteArray
    ): String {
        return safeApiCall {
            client.post("$BASE_URL/image") {
                setBody(
                    buildSinglePartFormData(fileName, fileBytes, "file")
                )
            }
        }
    }

    override suspend fun isDukanNameTaken(name: String): Boolean {
        return safeApiCall<DukanNameResponse> {
            client.get("$BASE_URL/available?name=$name").body()
        }.available.not()
    }

    override suspend fun getDukansByCategory(
        categoryId: String,
        page: Int,
        size: Int
    ): PagedResult<Dukan> {
        val response: PageResponseDto<DukanDto> = safeApiCall {
            client.get("$BASE_URL/category/$categoryId") {
                parameter("page", page)
                parameter("size", size)
            }
        }
        return response.toDomain(mapper = DukanDto::toDomain)
    }

    override suspend fun addDukanToFavorites(dukanId: String) {
        safeApiCall<Unit> {
            client.post("$BASE_URL/favorites/$dukanId")
        }
    }

    override suspend fun removeDukanFromFavorites(dukanId: String) {
        safeApiCall<Unit> {
            client.delete("$BASE_URL/favorites/$dukanId")
        }
    }

    override suspend fun getFavoriteDukans(): List<Dukan> {
        return safeApiCall<List<DukanDto>> {
            client.get("$BASE_URL/favorites")
        }.map { it.toDomain() }
    }

    override suspend fun isDukanFavorite(dukanId: String): Boolean {
        return safeApiCall<Boolean> {
            client.get("$BASE_URL/favorites/$dukanId/status").body()
        }
    }

    companion object {
        private const val BASE_URL = "/dukan"
    }
}
