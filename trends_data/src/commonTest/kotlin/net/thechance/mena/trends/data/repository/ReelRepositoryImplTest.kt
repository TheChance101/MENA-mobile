package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isSuccess
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.repository.util.createReelsRepository
import net.thechance.mena.trends.data.repository.util.deleteReelResponse
import net.thechance.mena.trends.data.repository.util.fakeReelList
import net.thechance.mena.trends.data.repository.util.updateReelResponse
import net.thechance.mena.trends.data.repository.util.uploadReelResponse
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
    fun `should update reel successfully`() = runTest {
        repository = createReelsRepository { id, description, categoryIds ->
            updateReelResponse(id, description, categoryIds, HttpStatusCode.NoContent)
        }

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
    fun `should Upload reel successfully` () {
        repository = createReelsRepository(
            uploadReel = { uploadReelResponse(HttpStatusCode.OK) }
        )

        val result = runCatching {
            repository.uploadReel(
                name = fakeName,
                mimeType = fakeMimeType,
                size = fakeSize,
                bytes = fakeBytes
            )
        }
        assertThat(result).isSuccess()
    }

    @Test
    fun `should upload reel and emit correct progress updates`() = runTest {
        repository = createReelsRepository(
            uploadReel = { uploadReelResponse(HttpStatusCode.OK) }
        )

        val progressUpdates = repository.uploadReel(
            name = fakeName,
            mimeType = fakeMimeType,
            size = fakeSize,
            bytes = fakeBytes
        ).toList()

        assertThat(progressUpdates).isNotEmpty()
        val lastProgress = progressUpdates.last()

        assertThat(lastProgress.numberOfUploadedBytes).isEqualTo(fakeSize)
        assertThat(lastProgress.totalBytes).isEqualTo(fakeSize)
    }

    companion object {
        val fakeSize = 1000L
        val fakeBytes = ByteArray(fakeSize.toInt()) { 1 }
        val fakeMimeType = "mp4"
        val fakeName = "test_video"
    }
}