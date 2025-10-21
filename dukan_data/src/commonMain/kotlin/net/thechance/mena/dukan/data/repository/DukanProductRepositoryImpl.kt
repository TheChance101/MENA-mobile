package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.product.CreateProductResponse
import net.thechance.mena.dukan.data.dto.product.ProductDto
import net.thechance.mena.dukan.data.mapper.toCreateProductRequest
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.util.constants.EndPoints.PRODUCT_BASE_PATH
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.model.CreateProductParams
import net.thechance.mena.dukan.domain.util.PagedResult

class DukanProductRepositoryImpl(
    private val client: HttpClient
) : ProductRepository {

    override suspend fun createProduct(params: CreateProductParams): String {
        return safeApiCall<CreateProductResponse> {
            client.post("${PRODUCT_BASE_PATH}/create") {
                contentType(ContentType.Application.Json)
                setBody(params.toCreateProductRequest())
            }
        }.productId
    }

    override suspend fun getProductsByShelfId(
        shelfId: String,
        page: Int,
        size: Int
    ): PagedResult<Product> {
        val response: PageResponseDto<ProductDto> = safeApiCall {
            client.get("${PRODUCT_BASE_PATH}/$shelfId") {
                parameter("page", page)
                parameter("size", size)
            }
        }
        return response.toDomain(mapper = ProductDto::toDomain)
    }

}