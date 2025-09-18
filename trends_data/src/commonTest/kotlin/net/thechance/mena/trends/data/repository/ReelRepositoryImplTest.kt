package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemoteResponse
import kotlin.test.BeforeTest
import kotlin.test.Test

class ReelRepositoryImplTest {

    private lateinit var httpClient: HttpClient
    private lateinit var repository : ReelsRepositoryImpl

    @BeforeTest
    fun setUp() {
        httpClient = createReelsHttpClient()
        repository = ReelsRepositoryImpl(httpClient)
    }

    @Test
    fun `should return list of reels mapped to entity successfully when the user has already reels`() =
        runTest {

            val reels = repository.getAllReels(pageNumber = 1)

            assertThat(reels).isEqualTo(fakeReelList)

        }

    @Test
    fun `should return empty list of reels when the user has no reels`() = runTest {

    }


}