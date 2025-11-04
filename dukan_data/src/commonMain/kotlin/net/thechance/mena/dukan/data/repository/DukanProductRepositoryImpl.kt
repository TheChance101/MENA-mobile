package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
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
import net.thechance.mena.dukan.data.util.network.buildMultiPartFormData
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CreateProductParams
import net.thechance.mena.dukan.domain.repository.ProductRepository
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
            client.get(PRODUCT_BASE_PATH) {
                parameter("page", page)
                parameter("size", size)
                parameter("shelfId",shelfId)
            }
        }
        return response.toDomain(mapper = ProductDto::toDomain)
    }

    override suspend fun uploadProductImages(
        fileName: List<String>,
        fileBytes: List<ByteArray>,
        productId: String
    ): List<String> {
        require(fileName.size == fileBytes.size) {
            "fileNames and fileBytes must have the same size."
        }

        val parts: List<Pair<String, ByteArray>> = fileName.zip(fileBytes)

        return safeApiCall {
            client.post("${PRODUCT_BASE_PATH}/images/$productId") {
                accept(ContentType.Application.Json)
                setBody(buildMultiPartFormData(parts, fieldName = "files"))
            }
        }
    }

    override suspend fun getProductDetails(productId: String): Product {
        return safeApiCall<ProductDto> {
            client.get("${PRODUCT_BASE_PATH}/$productId")
        }.toDomain()
    }

}