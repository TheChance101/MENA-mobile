package net.thechance.mena.trends.presentation.screen.upload_reel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.domain.validation.VideoValidator
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.formatBytes
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UploadReelViewModel(
    @Provided private val reelsRepository: ReelsRepository,
    @Provided private val videoValidator: VideoValidator,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UploadReelScreenState, UploadReelScreenEffect>(
    UploadReelScreenState()
), UploadReelInteractionListener {

    private var uploadingTrendJob: Job? = null

    override fun onRetrieveVideo(file: FileUiState) {
        updateState {
            UploadReelScreenState(
                selectedFile = file.copy(sizeText = formatBytes(file.size)),
            )
        }
        tryToExecute(
            block = { validateFile() },
            onError = ::onValidationError,
            onSuccess = { onValidationSuccess() },
            dispatcher = defaultDispatcher
        )
    }

    private suspend fun validateFile() {
        val selectedFile = state.value.selectedFile

        videoValidator.validateSize(selectedFile.size)
        reelsRepository.getReelDuration(selectedFile.filePath)?.let { duration ->
            videoValidator.validateDuration(duration)
        }
    }

    private fun onValidationSuccess() {
        updateState { copy(errorState = null) }
        uploadTrend()
    }

    private fun onValidationError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    private fun uploadTrend() {
        uploadingTrendJob = tryToCollectFlow(
            block = {
                reelsRepository.uploadReel(
                    filePath = state.value.selectedFile.filePath,
                    fileName = state.value.selectedFile.name,
                    size = state.value.selectedFile.size
                )
            },
            onStart = ::onUploadStarted,
            onNewValue = ::onCollectUploadProgress,
            onError = ::onUploadError,
            onEnd = ::onUploadCompleted,
            dispatcher = defaultDispatcher
        )
    }

    private fun onUploadStarted() {
        updateState { copy(uploadingState = UploadReelScreenState.UploadingReelState.UPLOADING) }
    }

    private fun onCollectUploadProgress(progress: UploadReelProgress) {
        val uploadingProgress = progress.numberOfUploadedBytes / progress.totalBytes.toFloat()
        updateState {
            copy(
                reelId = progress.reelId.takeIf(String::isNotEmpty),
                uploadingProgress = uploadingProgress,
                sizeUploaded = formatBytes(
                    bytes = progress.numberOfUploadedBytes,
                    withUnit = false
                )
            )
        }
    }

    private fun onUploadError(errorState: ErrorState) {
        updateState {
            copy(
                uploadingState = UploadReelScreenState.UploadingReelState.FAILED,
                errorState = errorState
            )
        }
    }

    private fun onUploadCompleted() {
        updateState {
            copy(
                uploadingState = UploadReelScreenState.UploadingReelState.SUCCESS,
            )
        }
        extractFrame()
    }

    private fun extractFrame() {
        tryToExecute(
            block = {
                reelsRepository.getReelThumbnail(
                    filePath = state.value.selectedFile.filePath,
                    timeMs = 1L
                )
            },
            onSuccess = ::onExtractFrameSuccess,
            onError = ::onExtractFrameError,
            onStart = { updateState { copy(isThumbnailLoading = true) } },
            onEnd = { updateState { copy(isThumbnailLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun onExtractFrameSuccess(thumbnail: ByteArray?) {
        updateState { copy(
            thumbnail = thumbnail,
            isNextButtonEnabled = true,
        ) }
    }

    private fun onExtractFrameError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    override fun onNextClick() {
        uploadThumbnail()
    }

    private fun uploadThumbnail(){
        tryToExecute(
            block = {
                state.value.reelId?.let { reelId ->
                    reelsRepository.uploadReelThumbnail(
                        reelId = reelId,
                        fileName = state.value.selectedFile.name,
                        thumbnail = state.value.thumbnail ?: ByteArray(0),
                    )
                }
            },
            onStart = ::onUploadThumbnailStarted,
            onEnd = ::onUploadThumbnailFinished,
            onSuccess = { onUploadThumbnailSuccess() },
            onError = ::onUploadThumbnailError,
            dispatcher = defaultDispatcher
        )
    }

    private fun onUploadThumbnailStarted() {
        updateState { copy(isNextButtonLoading = true) }
    }

    private fun onUploadThumbnailFinished() {
        updateState { copy(isNextButtonLoading = false) }
    }

    private fun onUploadThumbnailSuccess(){
        state.value.reelId?.let {
            sendEffect(UploadReelScreenEffect.NavigateToAddDescription(it))
        }
    }

    private fun onUploadThumbnailError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    override fun onBackClick() {
        sendEffect(UploadReelScreenEffect.NavigateBack)
    }

    override fun onCancelUploadClick() {
        uploadingTrendJob?.cancel()
        updateState { UploadReelScreenState() }
    }

    override fun onDeleteVideoClick() {
        tryToExecute(
            block = { state.value.reelId?.let { reelsRepository.deleteReelById(id = it) } },
            onSuccess = { updateState { UploadReelScreenState() } },
            onError = { errorState -> updateState { copy(errorState = errorState) } },
            dispatcher = defaultDispatcher
        )
    }

    override fun onRetryUploadClick() {
        uploadTrend()
    }
}
