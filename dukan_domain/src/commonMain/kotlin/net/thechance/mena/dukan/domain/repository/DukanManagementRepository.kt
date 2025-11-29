package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.MyDukanStatus

interface DukanManagementRepository {
    suspend fun createDukan(dukan: Dukan)
    suspend fun isDukanNameTaken(name: String): Boolean
    suspend fun getMyDukanStatus(): MyDukanStatus?
    suspend fun getDukanDetailsByDukanId(dukanId: String): Dukan
    suspend fun getDukanStyles(): List<Dukan.Style>
    suspend fun getDukanColors(): List<Color>
    suspend fun getCategories(): List<Category>
    suspend fun uploadDukanImage(fileName: String, fileBytes: ByteArray): String
    suspend fun updateFavoriteDukanStatus(dukanId: String)
    suspend fun getDukanActivationStatus(): Dukan.Activation
}