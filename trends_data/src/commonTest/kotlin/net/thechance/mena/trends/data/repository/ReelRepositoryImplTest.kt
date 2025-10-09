package net.thechance.mena.trends.data.repository

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import dev.mokkery.verify
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import kotlinx.io.RawSource
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.repository.util.FileReaderMock
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

    private lateinit var networkClient: NetworkClient
    private val fileReader = FileReaderMock()
    private lateinit var repository: ReelsRepositoryImpl

    @Test
    fun `should return list of reels mapped to entity successfully when the user has already reels`() =
        runTest {

            networkClient = createReelsHttpClient { getReelsResponse() }
            repository = ReelsRepositoryImpl(networkClient, fileReader)

            val reels = repository.getAllReels(pageNumber = 1)

            assertThat(reels).isEqualTo(fakeReelList)
        }

    @Test
    fun `should delete reel successfully when valid id provided`() = runTest {

        networkClient = createReelsHttpClient { deleteReelResponse() }
        repository = ReelsRepositoryImpl(networkClient, fileReader)
        val result = runCatching { repository.deleteReelById("1") }

        assertThat(result).isSuccess()
    }

    @Test
    fun `should update reel successfully`() = runTest {

        networkClient =
            createReelsHttpClient { updateReelResponse("1", "Updated description", listOf("cat1")) }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

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
    fun `uploadReel should emit final progress with reelId when uploading success`() = runTest {
        networkClient = createReelsHttpClient { uploadReelResponse() }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

        repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).test {
            assertThat(awaitItem().reelId).isEqualTo("1")
            awaitComplete()
        }
    }

    @Test
    fun `uploadReel should emit final progress with number of uploaded bytes equal to size when success`() = runTest {
        networkClient = createReelsHttpClient { uploadReelResponse() }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

        repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).test {
            assertThat(awaitItem().numberOfUploadedBytes).isEqualTo(FAKE_SIZE)
            awaitComplete()
        }
    }

    @Test
    fun `uploadReel should emit final progress with total size equal to file size when success`() = runTest {
        networkClient = createReelsHttpClient { uploadReelResponse() }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

        repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).test {
            assertThat(awaitItem().totalBytes).isEqualTo(FAKE_SIZE)
            awaitComplete()
        }
    }

    @Test
    fun `uploadReel should call fileReader with correct file path`() = runTest {
        networkClient = createReelsHttpClient { uploadReelResponse() }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

        repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).collect()

        verify { fileReader.readFile(FAKE_FILE_PATH) }
    }

    @Test
    fun `uploadReel should fail when request fails with request timeout`() = runTest {
        networkClient = createReelsHttpClient {
            uploadReelResponse(status = HttpStatusCode.RequestTimeout)
        }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

        assertFails {
            repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).collect()
        }
    }

    @Test
    fun `uploadReel should fail when server send error`() = runTest {
        networkClient = createReelsHttpClient {
            uploadReelResponse(status = HttpStatusCode.InternalServerError)
        }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

        assertFails {
            repository.uploadReel(FAKE_FILE_PATH, FAKE_FILE_NAME, FAKE_SIZE).collect()
        }
    }

    @Test
    fun `should upload reel thumbnail successfully when valid id provided`() = runTest {
        networkClient = createReelsHttpClient { uploadReelThumbnailResponse() }
        repository = ReelsRepositoryImpl(networkClient, fileReader)

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
        repository = ReelsRepositoryImpl(networkClient, fileReader)

        assertFails {
            repository.uploadReelThumbnail("1", FAKE_FILE_NAME, FAKE_BYTES)
        }
    }

    private companion object {
        const val FAKE_SIZE = 1000L
        val FAKE_BYTES = ByteArray(FAKE_SIZE.toInt()) { 1 }
        const val FAKE_FILE_PATH = "path/to/file"
        const val FAKE_FILE_NAME = "fileName"
    }
}