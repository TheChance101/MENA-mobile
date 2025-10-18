package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.data.dto.PatchUserCategoriesRequest
import net.thechance.mena.trends.data.dto.PatchUserCategoriesResponse
import net.thechance.mena.trends.data.dto.SubmitCategoriesRequestDto
import net.thechance.mena.trends.data.dto.UserStatusResponse
import net.thechance.mena.trends.data.mapper.toEntityList
import net.thechance.mena.trends.data.util.NetworkConstants.CATEGORIES_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkConstants.TRENDS_PATH
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
            networkClient.get("/$TRENDS_PATH/$CATEGORIES_ENDPOINT")
        }.toEntityList()
    }

    override suspend fun isCategoriesAlreadySelectedByUser(): Boolean {
        return safeApiCall<UserStatusResponse> {
            networkClient.get("/$TRENDS_PATH/$USER_STATUS_ENDPOINT")
        }.hasCategory.orFalse()
    }

    override suspend fun updateUserCategories(categoriesIds: List<String>) {
        safeApiCall<Unit> {
            networkClient.post("/$TRENDS_PATH/$CATEGORIES_ENDPOINT") {
                contentType(ContentType.Application.Json)
                setBody(SubmitCategoriesRequestDto(categoriesIds))
            }
        }
    }

    override suspend fun patchUserCategories(
        originalSelectedIds: List<String>,
        currentSelectedIds: List<String>
    ) {
        val toAdd = currentSelectedIds.filterNot { it in originalSelectedIds }
        val toRemove = originalSelectedIds.filterNot { it in currentSelectedIds }

        safeApiCall<PatchUserCategoriesResponse> {
            networkClient.patch("/$TRENDS_PATH/$CATEGORIES_ENDPOINT") {
                contentType(ContentType.Application.Json)
                setBody(
                    PatchUserCategoriesRequest(
                        categoriesIdsToAdd = toAdd,
                        categoriesIdsToRemove = toRemove
                    )
                )
            }
        }
    }
}