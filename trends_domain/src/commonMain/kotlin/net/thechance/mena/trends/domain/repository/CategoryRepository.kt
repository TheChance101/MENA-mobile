package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.Category

interface CategoryRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun isCategoriesAlreadySelectedByUser(): Boolean
    suspend fun initializeUserCategories(categoriesIds: List<String>)
    suspend fun updateUserCategories(
        originalSelectedIds: List<String>,
        currentSelectedIds: List<String>
    )
}