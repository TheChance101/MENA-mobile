package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Dukan

interface DukanRepository {
    suspend fun addDukan(dukan: Dukan)
    suspend fun getDukanStyles(): List<Dukan.Style>
    suspend fun getCategories(): List<Category>
    suspend fun getDukanColors(): List<Long>
    suspend fun getMyDukan(): Dukan
    suspend fun isDukanNameTaken(name: String): Boolean
    suspend fun isUserHasDukan(): Boolean
}