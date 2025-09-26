package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.repository.util.createReelsRepository
import net.thechance.mena.trends.data.repository.util.deleteReelResponse
import net.thechance.mena.trends.data.repository.util.fakeReelList
import net.thechance.mena.trends.data.repository.util.updateReelResponse
import kotlin.test.Test

internal class ReelRepositoryImplTest {

    private var repository = createReelsRepository()

    @Test
    fun `should return list of reels mapped to entity successfully when the user has already reels`() =
        runTest {

            val reels = repository.getAllReels(pageNumber = 1)

            assertThat(reels).isEqualTo(fakeReelList)
        }

    @Test
    fun `should delete reel successfully when valid id provided`() = runTest {

        repository = createReelsRepository(
            deleteReel = { id -> deleteReelResponse(id, HttpStatusCode.Companion.OK) }
        )

        val result = runCatching { repository.deleteReelById("1") }

        assertThat(result).isSuccess()
    }

    @Test
    fun `should update reel and map to entity successfully`() = runTest {
        repository = createReelsRepository { id, description, categoryIds ->
            updateReelResponse(id, description,categoryIds)
        }

        val result = repository.updateReelById(
            id = "1",
            description = "Updated description",
            categoryIds = listOf("cat1")
        )

        assertThat(result.id).isEqualTo("1")
        assertThat(result.description).isEqualTo("Updated description")
        assertThat(result.categories).hasSize(1)
        assertThat(result.categories.first().id).isEqualTo("cat1")
    }

}