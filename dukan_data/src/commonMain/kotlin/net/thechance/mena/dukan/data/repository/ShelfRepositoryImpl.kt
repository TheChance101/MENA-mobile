package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.statement.HttpResponse
import net.thechance.mena.dukan.data.repository.util.safeApiCall
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ShelfRepository

class ShelfRepositoryImpl (
    private val client: HttpClient
): ShelfRepository{
    override suspend fun createShelf(shelf: Shelf) {
        TODO("Not yet implemented")
    }

    override suspend fun getMyDukanShelves(): List<Shelf> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteShelf(shelfId: String): Boolean {
        val response: HttpResponse = safeApiCall {
            client.delete(
                urlString = "$BASE_URL/dukan/shelf/$shelfId",
            )
        }
        return response.status.value in SUCCESS
    }

    companion object {
        private const val BASE_URL = "/dukan"
        private val SUCCESS = 200..299
    }
}