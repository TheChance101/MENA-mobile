package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.ProductSearch
import net.thechance.mena.dukan.domain.model.DukanPreview
import net.thechance.mena.dukan.domain.util.PagedResult

interface SearchRepository {
    suspend fun findDukansByQuery(query: String, page: Int, size: Int): PagedResult<DukanPreview>
    suspend fun findProductsByQuery(query: String, page: Int, size: Int): PagedResult<ProductSearch>
}