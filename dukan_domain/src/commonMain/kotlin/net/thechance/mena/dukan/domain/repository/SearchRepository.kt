package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.util.PagedResult

interface SearchRepository {
    suspend fun findDukansByQuery(query: String, page: Int, size: Int): PagedResult<Dukan>
    suspend fun findProductsByQuery(query: String, page: Int, size: Int): PagedResult<Product>
}