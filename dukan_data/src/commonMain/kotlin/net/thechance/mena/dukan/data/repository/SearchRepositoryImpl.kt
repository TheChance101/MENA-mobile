package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.dukan.DukanSearchDto
import net.thechance.mena.dukan.data.dto.product.ProductSearchDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.mapper.toEntity
import net.thechance.mena.dukan.data.util.constants.EndPoints
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.ProductSearch
import net.thechance.mena.dukan.domain.model.DukanPreview
import net.thechance.mena.dukan.domain.repository.SearchRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SearchRepositoryImpl(
    private val client: HttpClient
): SearchRepository {
    override suspend fun findDukansByQuery(
        query: String,
        page: Int,
        size: Int
    ): PagedResult<DukanPreview> {
       return safeApiCall<PageResponseDto<DukanSearchDto>>{
           client.get(DUKAN_SEARCH_ENDPOINT) {
               parameter("query", query)
               parameter("page", page)
               parameter("size", size)
           }
        }.toDomain(mapper = DukanSearchDto::toEntity)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun finDukansByQueryInCategory(
        categoryId: String,
        query: String,
        page: Int,
        size: Int
    ): PagedResult<DukanPreview> {
        return safeApiCall<PageResponseDto<DukanSearchDto>>{
            client.get("${DUKAN_IN_CATEGORY_SEARCH_ENDPOINT}/$categoryId") {
                parameter("query", query)
                parameter("page", page)
                parameter("size", size)
            }
        }.toDomain(mapper = DukanSearchDto::toEntity)
    }

    override suspend fun findProductsByQuery(
        query: String,
        page: Int,
        size: Int
    ): PagedResult<ProductSearch> {
        return safeApiCall<PageResponseDto<ProductSearchDto>>{
            client.get(PRODUCT_SEARCH_ENDPOINT) {
                parameter("query", query)
                parameter("page", page)
                parameter("size", size)
            }
        }.toDomain(mapper = ProductSearchDto::toDomain)
    }

    companion object {
        const val DUKAN_SEARCH_ENDPOINT = "${EndPoints.DUKAN_BASE_PATH}/search"
        const val DUKAN_IN_CATEGORY_SEARCH_ENDPOINT = "${EndPoints.DUKAN_BASE_PATH}/search/categories"
        const val PRODUCT_SEARCH_ENDPOINT = "${EndPoints.DUKAN_BASE_PATH}/products/search"
    }
}