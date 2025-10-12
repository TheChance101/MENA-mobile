package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.MyDukanStatus

interface DukanRepository {
    suspend fun createDukan(dukan: Dukan)
    suspend fun getDukanStyles(): List<Dukan.Style>
    suspend fun getCategories(): List<Category>
    suspend fun getDukanColors(): List<Color>
    suspend fun isDukanNameTaken(name: String): Boolean
    suspend fun getMyDukanStatus(): MyDukanStatus?
    suspend fun uploadDukanImage(fileName: String, fileBytes: ByteArray): String
    suspend fun getDukanDetailsByDukanId(dukanId: String): Dukan
}