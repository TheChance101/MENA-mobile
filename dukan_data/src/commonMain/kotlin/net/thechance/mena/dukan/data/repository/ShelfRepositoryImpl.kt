package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.statement.HttpResponse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.repository.mapper.toCreateShelfRequest
import net.thechance.mena.dukan.data.repository.util.safeApiCall
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ShelfRepository

class ShelfRepositoryImpl (
    private val client: HttpClient
): ShelfRepository{
    override suspend fun createShelf(shelf: Shelf) {
        safeApiCall<Unit> {
            client.post("$BASE_URL/shelf/create") {
                contentType(ContentType.Application.Json)
                setBody(shelf.toCreateShelfRequest())
            }
        }
    }

    override suspend fun getMyDukanShelves(): List<Shelf> {
        // TODO: Replace with actual API call when backend is ready
        return listOf(
            Shelf(
                id = "shelf_1",
                name = "Electronics",
                dukanId = "dukan_123"
            ),
            Shelf(
                id = "shelf_2",
                name = "Clothing",
                dukanId = "dukan_123"
            ),
            Shelf(
                id = "shelf_3",
                name = "Books",
                dukanId = "dukan_123"
            )
        )
    }

    override suspend fun deleteShelf(shelfId: String): Boolean {
        val response: HttpResponse = safeApiCall {
            client.delete(
                urlString = "$BASE_URL/shelf/$shelfId",
            )
        }
        return isSuccess(response.status.value)
    }

    companion object {
        private const val BASE_URL = "/dukan"
        private fun isSuccess(status: Int) = status in 200..299
    }
}