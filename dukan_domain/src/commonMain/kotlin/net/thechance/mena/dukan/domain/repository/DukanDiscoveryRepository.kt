package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.DukanPreview
import net.thechance.mena.dukan.domain.util.PagedResult

interface DukanDiscoveryRepository {
    suspend fun getEditorPicksDukans(page: Int, size: Int): PagedResult<DukanPreview>
    suspend fun getBestAroundDukans(page: Int, size: Int): PagedResult<DukanPreview>
    suspend fun getDukansByCategory(
        categoryId: String,
        page: Int,
        size: Int
    ): PagedResult<DukanPreview>
}