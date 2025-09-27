package net.thechance.mena.trends.presentation.screen.upload_trend

import kotlinx.coroutines.Job
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.domain.validation.VideoMetaDataValidator
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.formatBytes
import net.thechance.mena.trends.presentation.shared.util.getVideoDuration
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UploadTrendViewModel(
    @Provided private val reelsRepository: ReelsRepository,
    @Provided private val validator: VideoMetaDataValidator
) : BaseViewModel<UploadTrendsScreenState, UploadTrendsScreenEffect>(
    UploadTrendsScreenState()
), UploadTrendInteractionListener {

    private var uploadingTrendJob: Job? = null

    override fun onRetrieveVideo(
        file: FileUiState,
        readBytes: suspend () -> ByteArray
    ) {
        tryToExecute(
            block = { validateFile(file, readBytes) },
            onError = ::onValidationError,
            onSuccess = ::onValidationSuccess
        )
    }

    private suspend fun validateFile(
        file: FileUiState,
        readBytes: suspend () -> ByteArray
    ): FileUiState {
        validator.validateSize(file.sizeInBytes)
        val bytes = readBytes()
        getVideoDuration(bytes)?.let { duration ->
            validator.validateDuration(duration)
        }
        return file.copy(bytes = bytes)
    }

    private fun onValidationError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    private fun onValidationSuccess(file: FileUiState) {
        updateState {
            copy(
                selectedFile = FileUiState(
                    name = file.name,
                    extension = file.extension,
                    sizeInBytes = file.sizeInBytes,
                    sizeInMegaBytes = formatBytes(file.sizeInBytes),
                    bytes = file.bytes
                )
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
            onComplete = ::onUploadCompleted
        )
    }

    private fun onUploadStarted() {
        updateState { copy(uploadingTrendState = UploadTrendsScreenState.UploadingTrendState.UPLOADING) }
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
                uploadingTrendState = UploadTrendsScreenState.UploadingTrendState.FAILED,
                errorState = errorState
            )
        }
    }

    private fun onUploadCompleted() {
        updateState {
            copy(
                uploadingTrendState = UploadTrendsScreenState.UploadingTrendState.SUCCESS,
                isNextButtonEnabled = true
            )
        }
    }

    override fun onBackClick() {
        sendEffect(UploadTrendsScreenEffect.NavigateBack)
    }

    override fun onEditVideoClick() {
        uploadingTrendJob?.cancel()
    }

    override fun onCancelUploadClick() {
        uploadingTrendJob?.cancel()
        updateState {
            copy(uploadingTrendState = UploadTrendsScreenState.UploadingTrendState.FAILED)
        }
    }

    override fun onDeleteVideoClick() {
        uploadingTrendJob?.cancel()
        updateState {
            copy(
                uploadingTrendState = UploadTrendsScreenState.UploadingTrendState.IDLE,
                uploadedMegaBytes = "",
                isNextButtonEnabled = false,
                selectedFile = FileUiState()
            )
        }
    }

    override fun onRetryUploadClick() {
        uploadingTrendJob?.cancel()
        uploadTrend(state.value.selectedFile)
    }

    override fun onNextClick() {
        sendEffect(UploadTrendsScreenEffect.NavigateToAddDescription(state.value.selectedFile.id))
    }
}
