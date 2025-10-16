package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.Category

interface CategoryRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun isCategoriesAlreadySelectedByUser(): Boolean
    suspend fun updateUserCategories(categoriesIds: List<String>)
}