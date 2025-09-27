package net.thechance.mena.trends.presentation.screen.upload_reel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.domain.validation.VideoMetaDataValidator
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.video_util.VideoDurationExtractor
import net.thechance.mena.trends.presentation.shared.util.video_util.formatBytes
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UploadReelViewModel(
    @Provided private val reelsRepository: ReelsRepository,
    @Provided private val videoValidator: VideoMetaDataValidator,
    @Provided private val videoDurationExtractor: VideoDurationExtractor,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UploadReelScreenState, UploadReelScreenEffect>(
    UploadReelScreenState()
), UploadReelInteractionListener {

    private var uploadingTrendJob: Job? = null

    override fun onRetrieveVideo(
        file: FileUiState,
        readBytes: suspend () -> ByteArray
    ) {
        tryToExecute(
            block = { validateFile(file, readBytes) },
            onError = ::onValidationError,
            onSuccess = ::onValidationSuccess,
            dispatcher = defaultDispatcher
        )
    }

    private suspend fun validateFile(
        file: FileUiState,
        readBytes: suspend () -> ByteArray
    ): FileUiState {
        videoValidator.validateSize(file.sizeInBytes)
        val bytes = readBytes()
        videoDurationExtractor.getDuration(bytes)?.let { duration ->
            videoValidator.validateDuration(duration)
        }
        return file.copy(bytes = bytes)
    }

    private fun onValidationError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    private fun onValidationSuccess(file: FileUiState) {
        updateState {
            copy(
                selectedFile = file.copy(sizeInMegaBytes = formatBytes(file.sizeInBytes)),
                errorState = null
            )
        }
        uploadTrend(file)
    }

    private fun uploadTrend(trendFile: FileUiState) {
        uploadingTrendJob = tryToCollectFlow(
            block = {
                reelsRepository.uploadReel(
                    name = trendFile.name,
                    mimeType = trendFile.extension,
                    size = trendFile.sizeInBytes,
                    bytes = trendFile.bytes
                )
            },
            onStart = ::onUploadStarted,
            onEach = ::onCollectEachFlow,
            onError = ::onUploadError,
            onEnd = ::onUploadCompleted,
            dispatcher = defaultDispatcher
        )
    }

    private fun onUploadStarted() {
        updateState { copy(uploadingTrendState = UploadReelScreenState.UploadingTrendState.UPLOADING) }
    }

    private fun onCollectEachFlow(progress: UploadReelProgress) {
        updateState {
            copy(
                uploadedMegaBytes = formatBytes(progress.numberOfUploadedBytes),
                selectedFile = state.value.selectedFile.copy(id = progress.reelId)
            )
        }
    }

    private fun onUploadError(errorState: ErrorState) {
        updateState {
            copy(
                uploadingTrendState = UploadReelScreenState.UploadingTrendState.FAILED,
                errorState = errorState
            )
        }
    }

    private fun onUploadCompleted() {
        updateState {
            copy(
                uploadingTrendState = UploadReelScreenState.UploadingTrendState.SUCCESS,
                isNextButtonEnabled = true
            )
        }
    }

    override fun onBackClick() {
        sendEffect(UploadReelScreenEffect.NavigateBack)
    }

    override fun onEditVideoClick() {
        uploadingTrendJob?.cancel()
    }

    override fun onCancelUploadClick() {
        uploadingTrendJob?.cancel()
        updateState { UploadReelScreenState() }
    }

    override fun onDeleteVideoClick() {
        uploadingTrendJob?.cancel()
        updateState { UploadReelScreenState() }
    }

    override fun onRetryUploadClick() {
        uploadingTrendJob?.cancel()
        uploadTrend(state.value.selectedFile)
    }

    override fun onNextClick() {
        sendEffect(UploadReelScreenEffect.NavigateToAddDescription(state.value.selectedFile.id))
    }
}
