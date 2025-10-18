package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.repository.dto.PageResponseDto
import net.thechance.mena.dukan.data.repository.dto.ShelfDto
import net.thechance.mena.dukan.data.repository.mapper.toCreateShelfRequest
import net.thechance.mena.dukan.data.repository.mapper.toDomain
import net.thechance.mena.dukan.data.repository.mapper.toShelf
import net.thechance.mena.dukan.data.repository.util.safeApiCall
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.domain.util.PagedResult

class ShelfRepositoryImpl(
    private val client: HttpClient
) : ShelfRepository {

    override suspend fun createShelf(shelf: Shelf) {
        safeApiCall<Unit> {
            client.post("$BASE_URL/shelf/create") {
                contentType(ContentType.Application.Json)
                setBody(shelf.toCreateShelfRequest())
            }
        }
    }

    override suspend fun getMyDukanShelves(): List<Shelf> {
        return safeApiCall<List<ShelfDto>> {
            client.get("$BASE_URL/shelf")
        }.map {
            it.toShelf()
        }
    }

    override suspend fun deleteShelf(shelfId: String) {
        safeApiCall<Unit> {
            client.delete(urlString = "$BASE_URL/shelf/$shelfId")
        }
    }

    override suspend fun getShelvesByDukanId(
        dukanId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagedResult<Shelf> {
        return safeApiCall<PageResponseDto<ShelfDto>> {
            client.get("$BASE_URL/shelf/$dukanId") {
                parameter("page", pageNumber)
                parameter("size", pageSize)
            }
        }.toDomain(mapper = ShelfDto::toShelf)
    }


    companion object {
        private const val BASE_URL = "/dukan"
    }
}