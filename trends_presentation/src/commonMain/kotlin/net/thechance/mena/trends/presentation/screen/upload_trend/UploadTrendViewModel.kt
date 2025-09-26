package net.thechance.mena.trends.presentation.screen.upload_trend

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.UploadReelsRepository
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
    @Provided private val uploadReelsRepository: UploadReelsRepository,
    @Provided private val validator: VideoMetaDataValidator
) : BaseViewModel<UploadTrendsScreenState, UploadTrendsScreenEffect>(
    UploadTrendsScreenState()
), UploadTrendInteractionListener {

    private var job: Job? = null

    override fun onRetrieveVideo(file: FileUiState, readBytes: suspend () -> ByteArray) {
        var bytes: ByteArray
        tryToExecute(
            block = {
                validator.validateSize(file.sizeInBytes)
                bytes = readBytes()
                getVideoDuration(bytes)?.let { validator.validateDuration(it) }
                file.copy(bytes = bytes)
            },
            onError = ::onValidationError,
            onSuccess = ::onValidationSuccess
        )
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
                    size = formatBytes(file.sizeInBytes),
                    bytes = file.bytes
                )
            )
        }
        uploadTrend(file)
    }

    private fun uploadTrend(trendFile: FileUiState) {
        job = tryToCollectFlow(
            block = {
                uploadReelsRepository.uploadReel(
                    name = trendFile.name,
                    mimeType = trendFile.extension,
                    size = trendFile.sizeInBytes,
                    bytes = trendFile.bytes
                )
            },
            onStart = ::onUploadStarted,
            onEach = ::onCollectEachFlow,
            onError = ::onUploadError,
            onComplete = ::onUploadCompleted,
            scope = viewModelScope
        )
    }

    private fun onUploadStarted() {
        updateState { copy(uploadingState = UploadTrendsScreenState.UploadingState.UPLOADING) }
    }

    private fun onCollectEachFlow(progress: UploadReelProgress) {
        updateState {
            copy(
                uploadedMegaBytes = formatBytes(progress.uploadedBytes),
                selectedFile = state.value.selectedFile.copy(id = progress.reelId)
            )
        }
    }

    private fun onUploadError(errorState: ErrorState) {
        updateState {
            copy(
                uploadingState = UploadTrendsScreenState.UploadingState.FAILED,
                errorState = errorState
            )
        }
    }

    private fun onUploadCompleted() {
        updateState {
            copy(
                uploadingState = UploadTrendsScreenState.UploadingState.SUCCESS,
                isNextButtonEnabled = true
            )
        }
    }

    override fun onBackClick() {
        sendEffect(UploadTrendsScreenEffect.NavigateBack)
    }

    override fun onEditVideoClick() {
        job?.cancel()
    }

    override fun onCancelUploadClick() {
        job?.cancel()
        updateState {
            copy(uploadingState = UploadTrendsScreenState.UploadingState.FAILED)
        }
    }

    override fun onDeleteVideoClick() {
        job?.cancel()
        updateState {
            copy(
                uploadingState = UploadTrendsScreenState.UploadingState.IDLE,
                uploadedMegaBytes = "",
                isNextButtonEnabled = false,
                selectedFile = FileUiState()
            )
        }
    }

    override fun onRetryUploadClick() {
        job?.cancel()
        uploadTrend(state.value.selectedFile)
    }

    override fun onNextClick() {
        sendEffect(UploadTrendsScreenEffect.NavigateToDescription(state.value.selectedFile.id))
    }
}
