package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.data.dto.UpdateUserCategoriesRequest
import net.thechance.mena.trends.data.dto.UpdateUserCategoriesResponse
import net.thechance.mena.trends.data.dto.SubmitCategoriesRequestDto
import net.thechance.mena.trends.data.dto.UserStatusResponse
import net.thechance.mena.trends.data.mapper.toEntityList
import net.thechance.mena.trends.data.util.NetworkConstants.CATEGORIES_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.USER_STATUS_ENDPOINT
import net.thechance.mena.trends.data.util.orFalse
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [CategoryRepository::class])
internal class CategoryRepositoryImpl(
    @Provided private val networkClient: HttpClient
) : CategoryRepository {

    override suspend fun getAllCategories(): List<Category> {
        return safeApiCall<List<CategoryDto>> {
            networkClient.get(CATEGORIES_ENDPOINT)
        }.toEntityList()
    }

    override suspend fun isCategoriesAlreadySelectedByUser(): Boolean {
        return safeApiCall<UserStatusResponse> {
            networkClient.get(USER_STATUS_ENDPOINT)
        }.hasCategory.orFalse()
    }

    override suspend fun initializeUserCategories(categoriesIds: List<String>) {
        safeApiCall<Unit> {
            networkClient.post(CATEGORIES_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(SubmitCategoriesRequestDto(categoriesIds))
            }
        }
    }

    override suspend fun updateUserCategories(
        originalSelectedIds: List<String>,
        currentSelectedIds: List<String>
    ) {
        val toAdd = currentSelectedIds.filterNot { it in originalSelectedIds }
        val toRemove = originalSelectedIds.filterNot { it in currentSelectedIds }

        safeApiCall<UpdateUserCategoriesResponse> {
            networkClient.patch(CATEGORIES_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateUserCategoriesRequest(
                        categoriesIdsToAdd = toAdd,
                        categoriesIdsToRemove = toRemove
                    )
                )
            }
        }
    }
}