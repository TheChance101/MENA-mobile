package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.toByteArray
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.remote.dto.UpdateUserCategoriesRequest
import net.thechance.mena.trends.data.remote.mapper.toEntityList
import net.thechance.mena.trends.data.remote.repository.CategoryRepositoryImpl
import net.thechance.mena.trends.data.repository.util.createCategoryHttpClient
import net.thechance.mena.trends.data.repository.util.getAllCategoriesResponse
import net.thechance.mena.trends.data.repository.util.jsonSerialization
import net.thechance.mena.trends.data.repository.util.mockCategories
import net.thechance.mena.trends.data.repository.util.patchUserInterestsResponse
import net.thechance.mena.trends.data.repository.util.updateInterestsResponse
import net.thechance.mena.trends.domain.repository.CategoryRepository
import kotlin.test.Test

internal class CategoryRepositoryImplTest {

    private lateinit var repository: CategoryRepository
    private lateinit var networkClient: HttpClient

    @Test
    fun `getAllCategories should return list of mapped categories`() = runTest {

        networkClient = createCategoryHttpClient { getAllCategoriesResponse() }
        repository = CategoryRepositoryImpl(networkClient)

        val result = repository.getAllCategories()

        assertThat(result).isEqualTo(mockCategories.toEntityList())
    }

    @Test
    fun `updateUserInterests should succeed when API call is successful`() = runTest {

        networkClient = createCategoryHttpClient { updateInterestsResponse() }
        repository = CategoryRepositoryImpl(networkClient)

        repository.initializeUserCategories(listOf("uuid1"))

        verifySuspend { networkClient = createCategoryHttpClient { updateInterestsResponse() } }
    }

    @Test
    fun `updateUserCategories should succeed when API call is successful`() = runTest {
        networkClient = createCategoryHttpClient { patchUserInterestsResponse() }
        repository = CategoryRepositoryImpl(networkClient)

        repository.updateUserCategories(
            originalSelectedIds = listOf("uuid1", "uuid2", "uuid3"),
            currentSelectedIds = listOf("uuid2", "uuid3", "uuid4", "uuid5")
        )

        verifySuspend { networkClient = createCategoryHttpClient { patchUserInterestsResponse() } }
    }

    @Test
    fun `updateUserCategories should calculate correct categories to add and remove`() = runTest {
        val (toAdd, toRemove) = capturePatchRequest(
            originalIds = listOf("uuid1", "uuid2", "uuid3"),
            currentIds = listOf("uuid2", "uuid3", "uuid4", "uuid5")
        )

        assertThat(toAdd).isEqualTo(listOf("uuid4", "uuid5"))
        assertThat(toRemove).isEqualTo(listOf("uuid1"))
    }

    @Test
    fun `updateUserCategories should send only new categories when adding`() = runTest {
        val (toAdd, toRemove) = capturePatchRequest(
            originalIds = listOf("uuid1", "uuid2"),
            currentIds = listOf("uuid1", "uuid2", "uuid3")
        )

        assertThat(toAdd).isEqualTo(listOf("uuid3"))
        assertThat(toRemove).isEqualTo(emptyList())
    }

    @Test
    fun `updateUserCategories should send only removed categories when removing`() = runTest {
        val (toAdd, toRemove) = capturePatchRequest(
            originalIds = listOf("uuid1", "uuid2", "uuid3"),
            currentIds = listOf("uuid2")
        )

        assertThat(toAdd).isEqualTo(emptyList())
        assertThat(toRemove).isEqualTo(listOf("uuid1", "uuid3"))
    }

    @Test
    fun `updateUserCategories should send empty lists when no changes`() = runTest {
        val (toAdd, toRemove) = capturePatchRequest(
            originalIds = listOf("uuid1", "uuid2"),
            currentIds = listOf("uuid1", "uuid2")
        )

        assertThat(toAdd).isEqualTo(emptyList())
        assertThat(toRemove).isEqualTo(emptyList())
    }

    private suspend fun capturePatchRequest(
        originalIds: List<String>,
        currentIds: List<String>
    ): Pair<List<String>, List<String>> {
        var toAdd: List<String>? = null
        var toRemove: List<String>? = null

        networkClient = createCategoryHttpClient { request ->
            val body = jsonSerialization.decodeFromString<UpdateUserCategoriesRequest>(
                request.body.toByteArray().decodeToString()
            )
            toAdd = body.categoriesIdsToAdd
            toRemove = body.categoriesIdsToRemove
            patchUserInterestsResponse()
        }

        repository = CategoryRepositoryImpl(networkClient)
        repository.updateUserCategories(originalIds, currentIds)

        return Pair(toAdd.orEmpty(), toRemove.orEmpty())
    }
}