package net.thechance.mena.trends.presentation.screen.upload_trend

import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.domain.validation.VideoMetaDataValidator
import net.thechance.mena.trends.presentation.screen.upload_trend.UploadTrendsScreenState.UploadingTrendState
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.video_util.VideoDurationExtractor
import net.thechance.mena.trends.presentation.shared.util.video_util.formatBytes
import net.thechance.mena.trends.presentation.utils.TestExtensions
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UploadTrendViewModelTest: TestExtensions() {

    private val repository:ReelsRepository = mock {
        everySuspend { uploadReel(any(), any(), any(), any()) } returns emptyFlow()
    }
    private val validator: VideoMetaDataValidator = VideoMetaDataValidator()
    private val videoExtractor: VideoDurationExtractor = mock {
        everySuspend { getDuration(any()) } returns VALID_DURATION
    }
    private val viewModel by lazy {
        UploadTrendViewModel(
            reelsRepository = repository,
            videoValidator = validator,
            videoDurationExtractor = videoExtractor,
            defaultDispatcher = testDispatcher
        )
    }

    @Test
    fun `onRetrieveVideo should call validateSize`() = runTest(testDispatcher) {
        viewModel

        viewModel.onRetrieveVideo(defaultFile, ::defaultReadBytes)

        verify(exactly(1)) { validator.validateSize(any()) }
    }

    @Test
    fun `onRetrieveVideo should call validateDuration if size is valid`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(defaultFile, ::defaultReadBytes)

        verify(exactly(1)) { validator.validateDuration(any()) }
    }

    @Test
    fun `onRetrieveVideo should not call validateDuration if validation size throws exception`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(fileWithInvalidSize, ::defaultReadBytes)

        verify(exactly(0)) { validator.validateDuration(any()) }
    }

    @Test
    fun `onRetrieveVideo should not call validateDuration if duration is null`() = runTest(testDispatcher) {
        everySuspend { videoExtractor.getDuration(any()) } returns null

        viewModel
        viewModel.onRetrieveVideo(fileWithInvalidSize, ::defaultReadBytes)

        verify(exactly(0)) { validator.validateDuration(any()) }
    }

    @Test
    fun `onRetrieveVideo should update error state with FileTooLarge if size is not valid`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(fileWithInvalidSize, ::defaultReadBytes)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().errorState).isEqualTo(ErrorState.FileTooLarge)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRetrieveVideo should update state with valid selected file info`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(fileWithValidInfo, ::defaultReadBytes)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().selectedFile).isEqualTo(fileWithValidInfo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRetrieveVideo should call uploadTrend from repository when file is valid`() = runTest(testDispatcher) {
        viewModel.onRetrieveVideo(fileWithValidInfo, ::defaultReadBytes)
        advanceUntilIdle()

        verify(exactly(1)) { repository.uploadReel(any(), any(), any(), any()) }
    }

    @Test
    fun `onCancelUploadClick should cancel the job and update state to IDLE`() = runTest(testDispatcher) {
        viewModel.onCancelUploadClick()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.uploadingTrendState).isEqualTo(UploadingTrendState.IDLE)
            assertThat(state.isNextButtonEnabled).isFalse()
            assertThat(state.errorState).isNull()
            assertThat(state.uploadedMegaBytes).isEmpty()
        }
    }

    @Test
    fun `onRetryUploadClick should call uploadTrend from repository`() = runTest(testDispatcher) {
        viewModel.onRetryUploadClick()
        advanceUntilIdle()

        verify(exactly(1)) { repository.uploadReel(any(), any(), any(), any()) }
    }

    @Test
    fun `onBackClick should send NavigateBack effect`() = runTest(testDispatcher) {
        viewModel.onBackClick()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(UploadTrendsScreenEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNextClick should send NavigateToAddDescription effect`() = runTest(testDispatcher) {
        viewModel.onNextClick()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(UploadTrendsScreenEffect.NavigateToAddDescription(""))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteVideoClick should cancel job and update uploading state to IDLE`() = runTest {
        viewModel.onDeleteVideoClick()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.uploadingTrendState).isEqualTo(UploadingTrendState.IDLE)
            assertThat(state.isNextButtonEnabled).isFalse()
            assertThat(state.errorState).isNull()
            assertThat(state.uploadedMegaBytes).isEmpty()
        }
    }

    @Test
    fun `onEditVideoClick should cancel job and update uploading state to IDLE`() = runTest {
        viewModel.onEditVideoClick()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.uploadingTrendState).isEqualTo(UploadingTrendState.IDLE)
        }
    }

    @Test
    fun `should update uploading state to FAILED if repository throws exception`() = runTest {
        everySuspend { repository.uploadReel(any(), any(), any(), any()) } throws Exception()

        viewModel.onRetrieveVideo(fileWithValidInfo, ::defaultReadBytes)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().uploadingTrendState).isEqualTo(
                UploadingTrendState.FAILED
            )
        }
    }

    @Test
    fun `should update state with progress of uploaded bytes when uploading`() = runTest {
        every { repository.uploadReel(any(), any(), any(), any()) } returns flowOf(uploadInProgress)

        viewModel.onRetrieveVideo(fileWithValidInfo, ::defaultReadBytes)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().uploadedMegaBytes).isEqualTo(formatBytes(uploadInProgress.numberOfUploadedBytes))
        }
    }

    @Test
    fun `should update uploading state to SUCCESS video uploaded successfully`() = runTest {
        viewModel.onRetrieveVideo(fileWithValidInfo, ::defaultReadBytes)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().uploadingTrendState).isEqualTo(
                UploadingTrendState.SUCCESS
            )
        }
    }

    @Test
    fun `should update state with reelId when uploaded successfully`() = runTest {
        every { repository.uploadReel(any(), any(), any(), any()) } returns flowOf(uploadDone)

        viewModel.onRetrieveVideo(fileWithValidInfo, ::defaultReadBytes)
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().selectedFile.id).isEqualTo(uploadDone.reelId)
        }
    }

    private companion object {
        const val INVALID_SIZE = 200 * 1024 * 1024 + 1L
        const val VALID_SIZE = 50 * 1024 * 1024 + 1L
        const val VALID_DURATION = 30_000L
        val byteArray = ByteArray(0)
        val uploadInProgress = UploadReelProgress("", 50, 100)
        val uploadDone = UploadReelProgress("id1", 100, 100)

        val defaultFile = FileUiState()
        val fileWithInvalidSize = defaultFile.copy(sizeInBytes = INVALID_SIZE)
        val fileWithValidInfo = FileUiState(
            id = "id1",
            name = "name1",
            extension = "mp4",
            sizeInBytes = VALID_SIZE,
            sizeInMegaBytes = formatBytes(VALID_SIZE),
            bytes = byteArray
        )

        fun defaultReadBytes() = byteArray
    }
}