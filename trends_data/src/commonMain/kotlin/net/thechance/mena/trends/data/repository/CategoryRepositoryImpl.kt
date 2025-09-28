package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import net.thechance.mena.trends.data.dto.CategoriesResponse
import net.thechance.mena.trends.data.dto.SubmitInterestsRequestDto
import net.thechance.mena.trends.data.dto.UserStatusResponse
import net.thechance.mena.trends.data.mapper.toEntityList
import net.thechance.mena.trends.data.util.NetworkConstants.CATEGORIES_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS_PATH
import net.thechance.mena.trends.data.util.NetworkConstants.USER_STATUS_ENDPOINT
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
        return safeApiCall<CategoriesResponse> {
            httpClient.get("/$TRENDS_PATH/$CATEGORIES_ENDPOINT")
        }.categories?.toEntityList() ?: emptyList()
    }

    override suspend fun isCategoriesAlreadySelectedByUser(): Boolean {
        return safeApiCall<UserStatusResponse> {
            httpClient.get("/$TRENDS_PATH/$USER_STATUS_ENDPOINT")
        }.hasCategory ?: false
    }

    override suspend fun updateUserInterestedCategories(categoriesIds: List<String>) {
        safeApiCall<Unit> {
            httpClient.post("/$TRENDS_PATH/$CATEGORIES_ENDPOINT") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(SubmitInterestsRequestDto(categoriesIds))
            }
        }
    }
}