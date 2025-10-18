package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.DukanPreview
import net.thechance.mena.dukan.domain.entity.MyDukanStatus
import net.thechance.mena.dukan.domain.util.PagedResult

interface DukanRepository {
    suspend fun createDukan(dukan: Dukan)
    suspend fun getDukanStyles(): List<Dukan.Style>
    suspend fun getCategories(): List<Category>
    suspend fun getDukanColors(): List<Color>
    suspend fun isDukanNameTaken(name: String): Boolean
    suspend fun getMyDukanStatus(): MyDukanStatus?
    suspend fun uploadDukanImage(fileName: String, fileBytes: ByteArray): String
    suspend fun getEditorPicksDukans(page: Int, size: Int): PagedResult<DukanPreview>
    suspend fun getBestAroundDukans(page: Int, size: Int): PagedResult<DukanPreview>
    suspend fun getDukansByCategory(categoryId: String, page: Int, size: Int): PagedResult<DukanPreview>
    suspend fun getDukanDetailsByDukanId(dukanId: String): Dukan
}