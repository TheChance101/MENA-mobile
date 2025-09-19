package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isSuccess
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.utils.createReelsRepository
import net.thechance.mena.trends.data.utils.deleteReelResponse
import net.thechance.mena.trends.data.utils.fakeReelList
import kotlin.test.BeforeTest
import kotlin.test.Test

class ReelRepositoryImplTest {

    private lateinit var repository: ReelsRepositoryImpl

    @BeforeTest
    fun setUp() {
        repository = createReelsRepository()
    }

    @Test
    fun `should return list of reels mapped to entity successfully when the user has already reels`() =
        runTest {

            val reels = repository.getAllReels(pageNumber = 1)

            assertThat(reels).isEqualTo(fakeReelList)
        }

    @Test
    fun `should delete reel successfully when valid id provided`() = runTest {

        repository = createReelsRepository(
            deleteReel = { id -> deleteReelResponse(id, HttpStatusCode.OK) }
        )

        val result = runCatching { repository.deleteReelById("1") }

        assertThat(result).isSuccess()
    }
}

