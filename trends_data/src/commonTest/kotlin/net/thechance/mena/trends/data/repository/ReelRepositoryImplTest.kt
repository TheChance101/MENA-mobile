package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isSuccess
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.repository.util.createReelsHttpClient
import net.thechance.mena.trends.data.repository.util.deleteReelResponse
import net.thechance.mena.trends.data.repository.util.fakeReelList
import net.thechance.mena.trends.data.repository.util.getReelsResponse
import net.thechance.mena.trends.data.repository.util.updateReelResponse
import net.thechance.mena.trends.data.repository.util.uploadReelResponse
import net.thechance.mena.trends.domain.repository.ReelsRepository
import kotlin.test.Test

internal class ReelRepositoryImplTest {

    private lateinit var repository: ReelsRepository
    private lateinit var networkClient: NetworkClient


    @Test
    fun `should return list of reels mapped to entity successfully when the user has already reels`() =
        runTest {

            networkClient = createReelsHttpClient { getReelsResponse() }
            repository = ReelsRepositoryImpl(networkClient)

            val reels = repository.getAllReels(pageNumber = 1)

            assertThat(reels).isEqualTo(fakeReelList)
        }

    @Test
    fun `should delete reel successfully when valid id provided`() = runTest {

        networkClient = createReelsHttpClient { deleteReelResponse() }
        repository = ReelsRepositoryImpl(networkClient)
        val result = runCatching { repository.deleteReelById("1") }

        assertThat(result).isSuccess()
    }

    @Test
    fun `should update reel successfully`() = runTest {

        networkClient = createReelsHttpClient { updateReelResponse("1", "Updated description", listOf("cat1")) }
        repository = ReelsRepositoryImpl(networkClient)

        val result = runCatching {
            repository.updateReelById(
                id = "1",
                description = "Updated description",
                categoryIds = listOf("cat1")
            )
        }

        assertThat(result).isSuccess()
    }

    @Test
    fun `should Upload reel successfully`() {

        networkClient = createReelsHttpClient { uploadReelResponse() }
        repository = ReelsRepositoryImpl(networkClient)

        val result = runCatching {
            repository.uploadReel(
                name = FAKE_NAME,
                mimeType = FAKE_MIME_TYPE,
                size = FAKE_SIZE,
                bytes = FAKE_BYTES
            )
        }
        assertThat(result).isSuccess()
    }

    @Test
    fun `should upload reel and emit correct progress updates`() = runTest {

        networkClient = createReelsHttpClient { uploadReelResponse() }
        repository = ReelsRepositoryImpl(networkClient)

        val progressUpdates = repository.uploadReel(
            name = FAKE_NAME,
            mimeType = FAKE_MIME_TYPE,
            size = FAKE_SIZE,
            bytes = FAKE_BYTES
        ).toList()

        assertThat(progressUpdates).isNotEmpty()
        val lastProgress = progressUpdates.last()

        assertThat(lastProgress.numberOfUploadedBytes).isEqualTo(FAKE_SIZE)
        assertThat(lastProgress.totalBytes).isEqualTo(FAKE_SIZE)
    }

    private companion object {
        const val FAKE_SIZE = 1000L
        val FAKE_BYTES = ByteArray(FAKE_SIZE.toInt()) { 1 }
        const val FAKE_MIME_TYPE = "mp4"
        const val FAKE_NAME = "test_video"
    }
}