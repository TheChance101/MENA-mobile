package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.TopDiscountedDukanPreview
import net.thechance.mena.dukan.domain.util.PagedResult

interface DukanDiscoveryRepository {
    suspend fun getEditorPicksDukans(page: Int, size: Int): PagedResult<Dukan>
    suspend fun getBestAroundDukans(page: Int, size: Int): PagedResult<Dukan>
    suspend fun getDukansByCategory(
        categoryId: String,
        page: Int,
        size: Int
    ): PagedResult<Dukan>
    suspend fun getTopDiscountedDukans(page: Int, size: Int): PagedResult<TopDiscountedDukanPreview>
}