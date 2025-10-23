package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Shelf

interface ShelfRepository {
    suspend fun createShelf(shelf: Shelf)
    suspend fun getMyDukanShelves(): List<Shelf>
    suspend fun deleteShelf(shelfId: String)
    suspend fun getShelvesByDukanId(
        dukanId: String,
        pageNumber: Int,
        pageSize: Int
    ): List<Shelf>
}