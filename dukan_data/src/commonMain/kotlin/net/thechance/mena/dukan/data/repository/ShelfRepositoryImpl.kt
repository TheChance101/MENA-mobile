package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.repository.dto.ShelfResponse
import net.thechance.mena.dukan.data.repository.mapper.toCreateShelfRequest
import net.thechance.mena.dukan.data.repository.mapper.toShelfList
import net.thechance.mena.dukan.data.repository.util.safeApiCall
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ShelfRepository

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
        return safeApiCall<List<ShelfResponse>> {
            client.get("$BASE_URL/shelf")
        }.toShelfList()
    }

    override suspend fun deleteShelf(shelfId: String): Boolean {
        TODO("Provide the return value")
    }

    companion object {
        private const val BASE_URL = "/dukan"
    }
}