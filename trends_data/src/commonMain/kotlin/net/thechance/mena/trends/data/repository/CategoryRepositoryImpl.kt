package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import net.thechance.mena.trends.data.dto.CategoryResponseDto
import net.thechance.mena.trends.data.dto.SubmitInterestsRequestDto
import net.thechance.mena.trends.data.dto.UserStatusResponse
import net.thechance.mena.trends.data.mapper.toEntityList
import net.thechance.mena.trends.data.util.NetworkConstants.CATEGORY
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [CategoryRepository::class])
internal class CategoryRepositoryImpl(
    @Provided private val httpClient: HttpClient
) : CategoryRepository {

    override suspend fun getAllCategories(): List<Category> {
        return safeApiCall<CategoryResponseDto> {
            httpClient.get("/$TRENDS/$CATEGORY")
        }.categories?.toEntityList() ?: emptyList()
    }

    override suspend fun isCategoriesAlreadySelectedByUser(): Boolean {
        return safeApiCall<UserStatusResponse> {
            httpClient.get("/$TRENDS/user/categories/status") // TODO: put string of end point
        }.value ?: false
    }

    override suspend fun updateUserInterestedCategories(categoriesIds: List<String>) {
        safeApiCall<Unit> {
            httpClient.post("/$TRENDS/$CATEGORY") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(SubmitInterestsRequestDto(categoriesIds))
            }
        }
    }
}