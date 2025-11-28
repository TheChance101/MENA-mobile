package net.thechance.mena.dukan.data.repository

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.shelf.ShelfDto
import net.thechance.mena.dukan.data.mapper.toCreateShelfRequest
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.mapper.toRequest
import net.thechance.mena.dukan.data.mapper.toShelf
import net.thechance.mena.dukan.data.util.constants.EndPoints.SHELF_BASE_PATH
import net.thechance.mena.dukan.data.util.network.DukanApi
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.model.UpdateShelfName
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.domain.util.PagedResult

class ShelfRepositoryImpl(
    private val client: DukanApi
) : ShelfRepository {

    override suspend fun createShelf(shelf: Shelf) {
        safeApiCall<Unit> {
            client.getClient().post("$SHELF_BASE_PATH/create") {
                contentType(ContentType.Application.Json)
                setBody(shelf.toCreateShelfRequest())
            }
        }
    }

    override suspend fun getMyDukanShelves(): List<Shelf> {
        return safeApiCall<List<ShelfDto>> {
            client.getClient().get(SHELF_BASE_PATH)
        }.map {
            it.toShelf()
        }
    }

    override suspend fun deleteShelf(shelfId: String) {
        safeApiCall<Unit> {
            client.getClient().delete(urlString = "$SHELF_BASE_PATH/$shelfId")
        }
    }

    override suspend fun updateShelf(shelfId: String, newShelfName: String) {
        safeApiCall<Unit> {
            client.getClient().put(urlString = "$SHELF_BASE_PATH/$shelfId") {
                contentType(ContentType.Application.Json)
                setBody(UpdateShelfName(newShelfName).toRequest())
            }
        }
    }

    override suspend fun getShelvesByDukanId(
        dukanId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagedResult<Shelf> {
        return safeApiCall<PageResponseDto<ShelfDto>> {
            client.getClient().get("$SHELF_BASE_PATH/$dukanId") {
                parameter("page", pageNumber)
                parameter("size", pageSize)
            }
        }.toDomain(mapper = ShelfDto::toShelf)
    }
}