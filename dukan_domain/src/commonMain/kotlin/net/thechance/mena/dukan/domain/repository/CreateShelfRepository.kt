package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Shelf

interface CreateShelfRepository {
    suspend fun createShelf(shelf: Shelf)
    suspend fun getMyDukanShelves(): List<Shelf>
    suspend fun deleteShelf(shelfId: String)
}