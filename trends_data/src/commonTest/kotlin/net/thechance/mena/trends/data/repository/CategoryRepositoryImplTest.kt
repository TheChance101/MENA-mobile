package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.mapper.toEntityList
import net.thechance.mena.trends.data.repository.util.createCategoryHttpClient
import net.thechance.mena.trends.data.repository.util.getAllCategoriesResponse
import net.thechance.mena.trends.data.repository.util.isCategoriesAlreadySelectedByUser
import net.thechance.mena.trends.data.repository.util.mockCategories
import net.thechance.mena.trends.data.repository.util.updateInterestsResponse
import net.thechance.mena.trends.domain.repository.CategoryRepository
import kotlin.test.Test

internal class CategoryRepositoryImplTest {

    private lateinit var repository: CategoryRepository
    private lateinit var networkClient: NetworkClient

    @Test
    fun `getAllCategories should return list of mapped categories`() = runTest {

        networkClient = createCategoryHttpClient { getAllCategoriesResponse() }
        repository = CategoryRepositoryImpl(networkClient)

        val result = repository.getAllCategories()

        assertThat(result).isEqualTo(mockCategories.toEntityList())
    }

    @Test
    fun `isCategoriesAlreadySelectedByUser should returns true if API returns non-empty list`() =
        runTest {

            networkClient = createCategoryHttpClient { isCategoriesAlreadySelectedByUser() }
            repository = CategoryRepositoryImpl(networkClient)

            val result = repository.isCategoriesAlreadySelectedByUser()

            assertThat(result).isTrue()
        }

    @Test
    fun `updateUserInterests should succeed when API call is successful`() = runTest {

        networkClient = createCategoryHttpClient { updateInterestsResponse() }
        repository = CategoryRepositoryImpl(networkClient)

        repository.updateUserInterestedCategories(listOf("uuid1"))

        verifySuspend { networkClient = createCategoryHttpClient { updateInterestsResponse() } }
    }
}