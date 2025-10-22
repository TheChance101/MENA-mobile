package net.thechance.mena.trends.presentation.screen.upload_reel

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.domain.model.UploadReelStatus
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.domain.validation.VideoValidator
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreenState.UploadingReelState
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.formatBytes
import net.thechance.mena.trends.presentation.utils.TestExtensions
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UploadReelViewModelTest : TestExtensions() {

    private val repository: ReelsRepository = mock {
        everySuspend { uploadReel(any(), any()) } returns emptyFlow()
        everySuspend { uploadReelThumbnail(any(), any()) } returns Unit
        everySuspend { getReelDuration(any()) } returns VALID_DURATION
        everySuspend { extractReelThumbnail(any(), any()) } returns byteArray
    }
    private val validator: VideoValidator = VideoValidator()

    private val viewModel by lazy {
        UploadReelViewModel(
            reelsRepository = repository,
            videoValidator = validator,
            defaultDispatcher = testDispatcher
        )
    }

    @Test
    fun `onRetrieveVideo should call validateSize when file selected`() = runTest(testDispatcher) {
        viewModel
        viewModel.onRetrieveVideo(validFile)

        verify(exactly(1)) { validator.validateSize(any()) }
    }

    @Test
    fun `onRetrieveVideo should not call getDuration if validation size throws exception`() =
        runTest(testDispatcher) {
            viewModel.onRetrieveVideo(invalidFile)

            verifySuspend(exactly(0)) { repository.getReelDuration(any()) }
        }

    @Test
    fun `onRetrieveVideo should not call validateDuration if duration is null`() = runTest(testDispatcher) {
        everySuspend { repository.getReelDuration(any()) } returns null

        viewModel.onRetrieveVideo(invalidFile)

        verify(exactly(0)) { validator.validateDuration(any()) }
    }

    @Test
    fun `onRetrieveVideo should update error state with FileTooLarge when file size exceed the max limit`() =
        runTest(testDispatcher) {
            viewModel.onRetrieveVideo(invalidFile)
            advanceUntilIdle()

            viewModel.state.test {
                assertThat(awaitItem().errorState).isEqualTo(ErrorState.FileTooLarge)
            }
        }

    @Test
    fun `onRetrieveVideo should call uploadTrend from repository when file is valid`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        verify(exactly(1)) { repository.uploadReel(any(), any()) }
    }

    @Test
    fun `onRetrieveVideo should update state with valid selected file info`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().selectedFile.name).isEqualTo(validFile.name)
        }
    }

    @Test
    fun `onRetrieveVideo should call extractFrame if uploading success`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        verifySuspend { repository.extractReelThumbnail(any(), any<Long>()) }
    }

    @Test
    fun `onRetrieveVideo should update state with thumbnail if extractFrame success`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().thumbnail).isNotNull()
        }
    }

    @Test
    fun `onRetrieveVideo should update state with error if extractFrame failed`() = runTest(testDispatcher) {
        everySuspend { repository.extractReelThumbnail(any(), any<Long>()) } throws Exception("")

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().errorState).isEqualTo(ErrorState.RequestFailed(""))
        }
    }

    @Test
    fun `onCancelUploadClick should update screen with the initial state`() = runTest(testDispatcher) {
        viewModel.onCancelUploadClick()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(initialScreenState)
        }
    }

    @Test
    fun `onRetryUploadClick should call uploadTrend from repository`() = runTest(testDispatcher) {
        viewModel.onRetryUploadClick()
        advanceUntilIdle()

        verify(exactly(1)) { repository.uploadReel(any(), any()) }
    }

    @Test
    fun `onBackClick should send NavigateBack effect`() = runTest(testDispatcher) {
        viewModel.onBackClick()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(UploadReelScreenEffect.NavigateBack)
        }
    }

    @Test
    fun `onNextClick should send NavigateToAddDescription effect if upload thumbnail success`() = runTest(testDispatcher) {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onNextClick()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(UploadReelScreenEffect.NavigateToAddDescription("id1"))
        }
    }

    @Test
    fun `onNextClick should update thumbnail in state if upload thumbnail success`() = runTest(testDispatcher) {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onNextClick()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().thumbnail).isNotNull()
        }
    }

    @Test
    fun `onNextClick should update state with error if upload thumbnail failed`() = runTest(testDispatcher) {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)
        everySuspend { repository.uploadReelThumbnail(any(), any()) } throws Exception("Failed")

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onNextClick()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().errorState).isEqualTo(ErrorState.RequestFailed("Failed"))
        }
    }

    @Test
    fun `onDeleteVideoClick should delete selected file and update state with initial state`() = runTest {
        viewModel.onDeleteVideoClick()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(initialScreenState)
        }
    }

    @Test
    fun `should update uploading state to FAILED if repository throws exception`() = runTest {
        everySuspend { repository.uploadReel(any(), any()) } throws Exception()

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().uploadingState).isEqualTo(UploadingReelState.FAILED)
        }
    }

    @Test
    fun `should update uploading state to SUCCESS video uploaded successfully`() = runTest {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().uploadingState).isEqualTo(UploadingReelState.SUCCESS)
        }
    }

    @Test
    fun `should update state with progress of uploaded bytes when uploading`() = runTest {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadInProgress)

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().sizeUploaded).isEqualTo(
                formatBytes(uploadInProgress.numberOfUploadedBytes, false)
            )
        }
    }

    @Test
    fun `should update state with reelId when uploaded successfully`() = runTest {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().reelId).isEqualTo(uploadDone.reelId)
        }
    }

    @Test
    fun `onNextClick should send NavigateToAddDescription effect`() = runTest(testDispatcher) {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onNextClick()

        viewModel.effect.test {
            val trendId = viewModel.state.value.reelId
            assertThat(awaitItem()).isEqualTo(
                UploadReelScreenEffect.NavigateToAddDescription(trendId ?: "")
            )
        }
    }

    @Test
    fun `onCancelUploadClick should reset screen state`() = runTest {
        viewModel.onCancelUploadClick()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(initialScreenState)
        }
    }

    @Test
    fun `onDeleteVideoClick should call deleteReelById if state has reel id`() = runTest {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)
        everySuspend { repository.deleteReelById(any()) } returns Unit

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onDeleteVideoClick()
        advanceUntilIdle()

        verifySuspend { repository.deleteReelById(any()) }
    }

    @Test
    fun `onDeleteVideoClick should reset screen state to initial`() = runTest {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)
        everySuspend { repository.deleteReelById(any()) } returns Unit

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onDeleteVideoClick()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(initialScreenState)
        }
    }

    @Test
    fun `onDeleteVideoClick should update errorState when repository throws exception`() = runTest {
        every { repository.uploadReel(any(), any()) } returns flowOf(uploadDone)
        everySuspend { repository.deleteReelById(any()) } throws Exception("Delete failed")

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onDeleteVideoClick()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().errorState).isEqualTo(ErrorState.RequestFailed("Delete failed"))
        }
    }

    private companion object {
        const val INVALID_SIZE = 200 * 1024 * 1024 + 1L
        const val VALID_SIZE = 50 * 1024 * 1024L
        const val VALID_DURATION = 30_000L
        val byteArray = ByteArray(0)
        val uploadInProgress = UploadReelStatus.UploadReelProgress( 50, 100)
        val uploadDone = UploadReelStatus.UploadReelSuccess("id1",)

        val initialScreenState = UploadReelScreenState()
        val initialFileState = FileUiState()
        val invalidFile = initialFileState.copy(size = INVALID_SIZE)
        val validFile = FileUiState(
            filePath = "path",
            name = "name1",
            size = VALID_SIZE
        )
    }
}