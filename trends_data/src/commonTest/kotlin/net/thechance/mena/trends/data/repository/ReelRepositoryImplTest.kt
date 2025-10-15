package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import dev.mokkery.verifySuspend
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.repository.util.VideoFileHandlerMock
import net.thechance.mena.trends.data.repository.util.createReelsHttpClient
import net.thechance.mena.trends.data.repository.util.deleteReelResponse
import net.thechance.mena.trends.data.repository.util.fakeReelList
import net.thechance.mena.trends.data.repository.util.getReelsResponse
import net.thechance.mena.trends.data.repository.util.updateReelResponse
import net.thechance.mena.trends.data.repository.util.uploadReelResponse
import net.thechance.mena.trends.data.repository.util.uploadReelThumbnailResponse
import kotlin.test.Test
import kotlin.test.assertFails

internal class ReelRepositoryImplTest {

    private var networkClient: NetworkClient = createReelsHttpClient { getReelsResponse() }
    private val videoHandler = VideoFileHandlerMock()
    private var repository = ReelsRepositoryImpl(networkClient, videoHandler)

    @Test
    fun `should return list of reels mapped to entity successfully when the user has already reels`() =
        runTest {

            networkClient = createReelsHttpClient { getReelsResponse() }
            repository = ReelsRepositoryImpl(networkClient, videoHandler)

            val reels = repository.getAllReels(pageNumber = 1)

            assertThat(reels).isEqualTo(fakeReelList)
        }

    @Test
    fun `should return feed reels mapped to entity successfully`() = runTest {
        networkClient = createReelsHttpClient { getReelsResponse() }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

        val result = repository.getFeedReels(page = 1)

        assertThat(result).isEqualTo(fakeReelList)
    }

    @Test
    fun `should delete reel successfully when valid id provided`() = runTest {

        networkClient = createReelsHttpClient { deleteReelResponse() }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)
        val result = runCatching { repository.deleteReelById("1") }

        assertThat(result).isSuccess()
    }

    @Test
    fun `should update reel successfully`() = runTest {

        networkClient =
            createReelsHttpClient { updateReelResponse("1", "Updated description", listOf("cat1")) }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

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
    fun `uploadReel should call fileReader with correct file path`() = runTest {
        networkClient = createReelsHttpClient { uploadReelResponse() }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

        repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).collect()

        verifySuspend { videoHandler.readFile(FAKE_FILE_PATH) }
    }

    @Test
    fun `uploadReel should fail when request fails with request timeout`() = runTest {
        networkClient = createReelsHttpClient {
            uploadReelResponse(status = HttpStatusCode.RequestTimeout)
        }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

        assertFails {
            repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).collect()
        }
    }

    @Test
    fun `uploadReel should fail when server send error`() = runTest {
        networkClient = createReelsHttpClient {
            uploadReelResponse(status = HttpStatusCode.InternalServerError)
        }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

        assertFails {
            repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).collect()
        }
    }

    @Test
    fun `should upload reel thumbnail successfully when valid id provided`() = runTest {
        networkClient = createReelsHttpClient { uploadReelThumbnailResponse() }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

        val result = runCatching {
            repository.uploadReelThumbnail("1", FAKE_FILE_NAME, FAKE_BYTES)
        }

        assertThat(result).isSuccess()
    }

    @Test
    fun `upload reel thumbnail should fail when server send error`() = runTest {
        networkClient = createReelsHttpClient {
            uploadReelThumbnailResponse(status = HttpStatusCode.InternalServerError)
        }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

        assertFails {
            repository.uploadReelThumbnail("1", FAKE_FILE_NAME, FAKE_BYTES)
        }
    }

    @Test
    fun `getReelDuration should call getDuration`() = runTest {
        repository.getReelDuration(FAKE_FILE_PATH)

        verifySuspend { videoHandler.getDuration(FAKE_FILE_PATH)  }
    }

    @Test
    fun `getReelDuration should return reel duration exactly as getDuration`() = runTest {
        val duration = repository.getReelDuration(FAKE_FILE_PATH)

        assertThat(duration).isEqualTo(FAKE_DURATION)
    }

    @Test
    fun `getReelThumbnail should call extractVideoFrame`() = runTest {
        repository.getReelThumbnail(FAKE_FILE_PATH)

        verifySuspend { videoHandler.extractVideoFrame(FAKE_FILE_PATH)  }
    }

    @Test
    fun `getReelDuration should return thumbnail bytearray when extractVideoFrame called`() = runTest {
        val thumbnailByteArray = repository.getReelThumbnail(FAKE_FILE_PATH)

        assertThat(thumbnailByteArray?.size).isEqualTo(2)
    }

    @Test
    fun `should throw exception when API fails in getAllReels`() = runTest {
        networkClient = createReelsHttpClient { throw Exception("Network Error") }
        repository = ReelsRepositoryImpl(networkClient, videoHandler)

        val result = runCatching { repository.getAllReels(pageNumber = 1) }

        assertThat(result.isFailure).isEqualTo(true)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Network Error")
    }
    private companion object {
        const val FAKE_SIZE = 1000L
        val FAKE_BYTES = ByteArray(FAKE_SIZE.toInt()) { 1 }
        const val FAKE_FILE_PATH = "path/to/file"
        const val FAKE_FILE_NAME = "fileName"
        const val FAKE_DURATION = 1000L
    }
}