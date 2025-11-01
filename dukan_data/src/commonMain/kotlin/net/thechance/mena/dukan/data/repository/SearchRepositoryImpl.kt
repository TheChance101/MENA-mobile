package net.thechance.mena.dukan.data.repository

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.SearchRepository
import net.thechance.mena.dukan.domain.util.PagedResult

class SearchRepositoryImpl: SearchRepository {
    override suspend fun findDukansByQuery(
        query: String,
        page: Int,
        size: Int
    ): PagedResult<Dukan> {
        return PagedResult(
            items = emptyList(),
            currentPage = page,
            totalPages = 0,
            pageSize = size,
            totalItems = 0
        )
    }

    override suspend fun findProductsByQuery(
        query: String,
        page: Int,
        size: Int
    ): PagedResult<Product> {
        return PagedResult(
            items = emptyList(),
            currentPage = page,
            totalPages = 0,
            pageSize = size,
            totalItems = 0
        )
    }
}