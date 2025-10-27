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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.domain.model.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.domain.validation.VideoValidator
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreenState.UploadingReelState
import net.thechance.mena.trends.presentation.shared.base.UploadReelErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.formatBytes
import net.thechance.mena.trends.presentation.utils.TestExtensions
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UploadReelViewModelTest : TestExtensions() {

    private val repository: ReelsRepository = mock {
        everySuspend { uploadReel(any(), any()) } returns FAKE_TREND_ID
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
                assertThat(awaitItem().errorState).isEqualTo(UploadReelErrorState.FileTooLarge)
            }
        }

    @Test
    fun `onRetrieveVideo should call uploadTrend from repository when file is valid`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        verifySuspend(exactly(1)) { repository.uploadReel(any(), any()) }
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
            assertThat(awaitItem().errorState).isNotNull()
        }
    }

    @Test
    fun `onClickRetryUpload should call uploadTrend from repository`() = runTest(testDispatcher) {
        viewModel.onClickRetryUpload()
        advanceUntilIdle()

        verifySuspend(exactly(1)) { repository.uploadReel(any(), any()) }
    }

    @Test
    fun `onClickBack should send NavigateBack effect`() = runTest(testDispatcher) {
        viewModel.onClickBack()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(UploadReelScreenEffect.NavigateBack)
        }
    }

    @Test
    fun `onClickNext should send NavigateToAddDescription effect if upload thumbnail success`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onClickNext()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(
                UploadReelScreenEffect.NavigateToAddDescription(FAKE_TREND_ID)
            )
        }
    }

    @Test
    fun `onClickNext should update thumbnail in state if upload thumbnail success`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onClickNext()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().thumbnail).isNotNull()
        }
    }

    @Test
    fun `onClickNext should update state with error if upload thumbnail failed`() = runTest(testDispatcher) {
        everySuspend { repository.uploadReelThumbnail(any(), any()) } throws Exception("Failed")

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onClickNext()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().errorState).isNotNull()
        }
    }

    @Test
    fun `onClickDeleteVideo should delete selected file and update state with initial state`() = runTest {
        viewModel.onClickDeleteVideo()
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
        every { repository.observeUploadReelProgress() } returns uploadInProgress

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().sizeUploaded).isEqualTo(
                formatBytes(uploadInProgress.value.numberOfUploadedBytes, false)
            )
        }
    }

    @Test
    fun `should update state with reelId when uploaded successfully`() = runTest {
        everySuspend { repository.uploadReel(any(), any()) } returns FAKE_TREND_ID

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().reelId).isEqualTo(FAKE_TREND_ID)
        }
    }

    @Test
    fun `onClickNext should send NavigateToAddDescription effect`() = runTest(testDispatcher) {
        everySuspend { repository.uploadReel(any(), any()) } returns FAKE_TREND_ID

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onClickNext()

        viewModel.effect.test {
            val trendId = viewModel.state.value.reelId
            assertThat(awaitItem()).isEqualTo(
                UploadReelScreenEffect.NavigateToAddDescription(trendId ?: "")
            )
        }
    }

    @Test
    fun `onClickCancelUpload should reset screen state`() = runTest {
        viewModel.onClickCancelUpload()

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(initialScreenState)
        }
    }

    @Test
    fun `onClickDeleteVideo should call deleteReelById if state has reel id`() = runTest {
        everySuspend { repository.deleteReelById(any()) } returns Unit

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onClickDeleteVideo()
        advanceUntilIdle()

        verifySuspend { repository.deleteReelById(any()) }
    }

    @Test
    fun `onClickDeleteVideo should reset screen state to initial`() = runTest {
        everySuspend { repository.deleteReelById(any()) } returns Unit

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onClickDeleteVideo()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(initialScreenState)
        }
    }

    @Test
    fun `onClickDeleteVideo should update errorState when repository throws exception`() = runTest {
        everySuspend { repository.deleteReelById(any()) } throws Exception("Delete failed")

        viewModel.onRetrieveVideo(validFile)
        advanceUntilIdle()

        viewModel.onClickDeleteVideo()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().errorState).isNotNull()
        }
    }

    private companion object {
        const val INVALID_SIZE = 200 * 1024 * 1024 + 1L
        const val VALID_SIZE = 50 * 1024 * 1024L
        const val VALID_DURATION = 30_000L
        const val FAKE_TREND_ID = "uuid"

        val byteArray = ByteArray(0)
        val uploadInProgress = MutableStateFlow(UploadReelProgress(50, 100))

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