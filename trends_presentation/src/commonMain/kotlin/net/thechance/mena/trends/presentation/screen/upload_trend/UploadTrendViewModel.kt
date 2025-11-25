package net.thechance.mena.trends.presentation.screen.upload_trend

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import net.thechance.mena.trends.domain.exception.MaxFileDurationExceededException
import net.thechance.mena.trends.domain.exception.MaxFileSizeExceededException
import net.thechance.mena.trends.domain.model.UploadTrendProgress
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.domain.validation.VideoValidator
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.base.UploadTrendErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.formatBytes
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UploadTrendViewModel(
    @Provided private val trendsRepository: TrendsRepository,
    @Provided private val videoValidator: VideoValidator,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UploadTrendScreenState, UploadTrendScreenEffect>(
    UploadTrendScreenState()
), UploadTrendInteractionListener {

    private var uploadingTrendJob: Job? = null

    init {
        observeUploadTrendProgress()
    }

    private fun observeUploadTrendProgress() {
        tryToCollectFlow(
            block = { trendsRepository.observeUploadTrendProgress() },
            onNewValue = ::onCollectUploadProgress,
            onError = ::onUploadError,
            dispatcher = defaultDispatcher
        )
    }

    private fun onCollectUploadProgress(progress: UploadTrendProgress) {
        val uploadingProgress = progress.numberOfUploadedBytes / progress.totalBytes.toFloat()
        updateState {
            copy(
                uploadingProgress = uploadingProgress,
                sizeUploaded = formatBytes(
                    bytes = progress.numberOfUploadedBytes,
                    withUnit = false
                )
            )
        }
    }

    override fun onRetrieveVideo(file: FileUiState) {
        updateState {
            UploadTrendScreenState(
                selectedFile = file.copy(sizeText = formatBytes(file.size)),
                errorState = null
            )
        }
        tryToExecute(
            block = { validateFile() },
            onError = ::onValidationError,
            onSuccess = { uploadTrend() },
            dispatcher = defaultDispatcher
        )
    }

    override fun onClickUploadCard() {
        sendEffect(UploadTrendScreenEffect.LaunchFilePicker)
    }

    private suspend fun validateFile() {
        val selectedFile = state.value.selectedFile

        videoValidator.validateSize(selectedFile.size)
        trendsRepository.getTrendDuration(selectedFile.filePath)?.let { duration ->
            videoValidator.validateDuration(duration)
        }
    }

    private fun onValidationError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
        sendEffect(UploadTrendScreenEffect.ShowErrorSnackbar(errorState = errorState))
    }

    private fun uploadTrend() {
        uploadingTrendJob = tryToExecute(
            block = {
                trendsRepository.uploadTrend(
                    filePath = state.value.selectedFile.filePath,
                    size = state.value.selectedFile.size
                )
            },
            onStart = ::onUploadStarted,
            onSuccess = ::onUploadTrendSuccess,
            onError = ::onUploadError,
            dispatcher = defaultDispatcher
        )
    }

    private fun onUploadStarted() {
        updateState { copy(uploadingState = UploadTrendScreenState.UploadingTrendState.UPLOADING) }
    }

    private fun onUploadTrendSuccess(trendId: String) {
        updateState {
            copy(
                trendId = trendId,
                uploadingState = UploadTrendScreenState.UploadingTrendState.SUCCESS
            )
        }
        extractFrame()
    }

    private fun onUploadError(errorState: ErrorState) {
        updateState {
            copy(
                uploadingState = UploadTrendScreenState.UploadingTrendState.FAILED,
                errorState = errorState
            )
        }
        sendEffect(UploadTrendScreenEffect.ShowErrorSnackbar(errorState = errorState))
    }

    private fun extractFrame() {
        tryToExecute(
            block = {
                trendsRepository.extractTrendThumbnail(state.value.selectedFile.filePath)
            },
            onSuccess = ::onExtractFrameSuccess,
            onError = ::onExtractFrameError,
            onStart = { updateState { copy(isThumbnailLoading = true) } },
            onEnd = { updateState { copy(isThumbnailLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun onExtractFrameSuccess(thumbnail: ByteArray?) {
        updateState {
            copy(
                thumbnail = thumbnail,
                isNextButtonEnabled = true,
            )
        }
    }

    private fun onExtractFrameError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
        sendEffect(UploadTrendScreenEffect.ShowErrorSnackbar(errorState = errorState))
    }

    override fun onClickNext() {
        uploadThumbnail()
    }

    private fun uploadThumbnail() {
        tryToExecute(
            block = {
                state.value.trendId?.let { trendId ->
                    trendsRepository.uploadTrendThumbnail(
                        trendId = trendId,
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

    private fun onUploadThumbnailSuccess() {
        state.value.trendId?.let {
            sendEffect(UploadTrendScreenEffect.NavigateToAddDescription(it))
        }
    }

    private fun onUploadThumbnailError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
        sendEffect(UploadTrendScreenEffect.ShowErrorSnackbar(errorState = errorState))
    }

    override fun onClickBack() {
        sendEffect(UploadTrendScreenEffect.NavigateBack)
    }

    override fun onClickCancelUpload() {
        uploadingTrendJob?.cancel()
        updateState { UploadTrendScreenState() }
    }

    override fun onClickDeleteVideo() {
        tryToExecute(
            block = { state.value.trendId?.let { trendsRepository.deleteTrendById(id = it) } },
            onSuccess = { updateState { UploadTrendScreenState() } },
            onError = { errorState ->
                updateState { copy(errorState = errorState) }
                sendEffect(UploadTrendScreenEffect.ShowErrorSnackbar(errorState = errorState))
            },
            dispatcher = defaultDispatcher
        )
    }

    override fun onClickRetryUpload() {
        uploadTrend()
    }

    override suspend fun mapExceptionToErrorState(
        throwable: Throwable,
        onError: suspend (ErrorState) -> Unit
    ) {
        when (throwable) {
            is MaxFileSizeExceededException -> UploadTrendErrorState.FileTooLarge
            is MaxFileDurationExceededException -> UploadTrendErrorState.DurationTooLarge
            else -> {
                super.mapExceptionToErrorState(throwable, onError)
                return
            }
        }.let { errorState ->
            Logger.e(TAG) { errorState.toString() }
            onError(errorState)
        }
    }

    private companion object {
        const val TAG = "UploadTrendErrorState"
    }
}
