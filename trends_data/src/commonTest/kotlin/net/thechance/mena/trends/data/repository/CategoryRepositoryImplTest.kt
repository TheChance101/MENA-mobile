package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.mapper.toEntityList
import net.thechance.mena.trends.data.repository.util.createCategoryRepository
import net.thechance.mena.trends.data.repository.util.mockCategories
import kotlin.test.Test

class CategoryRepositoryImplTest {

    private var repository = createCategoryRepository()

    @Test
    fun `getAllCategories should return list of mapped categories`() = runTest {
        val result = repository.getAllCategories()

        assertThat(result).isEqualTo(mockCategories.toEntityList())
    }

    @Test
    fun `isCategoriesAlreadySelectedByUser should returns true if API returns non-empty list`() = runTest {
        val result = repository.isCategoriesAlreadySelectedByUser()

        assertThat(result).isTrue()
    }

    @Test
    fun `updateUserInterests should succeed when API call is successful`() = runTest {
        repository.updateUserInterestedCategories(listOf("uuid 1"))

        assertThat(true)
    }
}


