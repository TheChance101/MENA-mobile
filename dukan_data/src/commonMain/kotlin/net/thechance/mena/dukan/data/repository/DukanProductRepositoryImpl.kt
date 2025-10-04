package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.repository.dto.PageResponseDto
import net.thechance.mena.dukan.data.repository.dto.product.CreateProductResponse
import net.thechance.mena.dukan.data.repository.dto.product.ProductDto
import net.thechance.mena.dukan.data.repository.mapper.toCreateProductRequest
import net.thechance.mena.dukan.data.repository.mapper.toDomain
import net.thechance.mena.dukan.data.repository.util.buildMultiPartFormData
import net.thechance.mena.dukan.data.repository.util.safeApiCall
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.util.CreateProductParams
import net.thechance.mena.dukan.domain.util.PagedResult

class DukanProductRepositoryImpl(
    private val client: HttpClient
): ProductRepository {

    override suspend fun createProduct(params: CreateProductParams): String {
        return safeApiCall<CreateProductResponse> {
            client.post("${BASE_URL}/create") {
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
            client.get("${BASE_URL}/$shelfId") {
                parameter("page", page)
                parameter("size", size)
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
            client.post("$BASE_URL/images/$productId") {
                // Don't set Content-Type manually; Ktor handles multipart + boundary.
                accept(io.ktor.http.ContentType.Application.Json)
                setBody(buildMultiPartFormData(parts, fieldName = "files"))
            }
        }
    }


    companion object{
        private const val BASE_URL = "/dukan/product"

    }
}