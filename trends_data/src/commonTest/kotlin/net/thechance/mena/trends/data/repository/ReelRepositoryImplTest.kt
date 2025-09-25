package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.repository.util.createReelsRepository
import net.thechance.mena.trends.data.repository.util.deleteReelResponse
import net.thechance.mena.trends.data.repository.util.fakeReelList
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
}