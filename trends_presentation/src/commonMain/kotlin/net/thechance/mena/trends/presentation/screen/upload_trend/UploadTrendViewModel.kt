package net.thechance.mena.trends.presentation.screen.upload_trend

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.UploadReelsRepository
import net.thechance.mena.trends.domain.usecase.validation.VideoMetaDataValidator
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
        tryToExecute(
            block = {
                validator.validateSize(file.sizeInBytes).also {
                    val bytes = readBytes()
                    getVideoDuration(bytes)?.let { validator.validateDuration(it) }
                    file.copy(bytes = bytes)
                }
            },
            onError = ::onValidationError,
            onSuccess = { onValidationSuccess(file) }
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
        state.value.selectedFile?.let { uploadTrend(it) }
    }

    private fun uploadTrend(trendFile: FileUiState?) {
        trendFile?.let {
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
    }

    private fun onUploadStarted() {
        updateState { copy(uploadingState = UploadTrendsScreenState.UploadingState.UPLOADING) }
    }

    private fun onCollectEachFlow(progress: UploadReelProgress) {
        val uploadedMB = progress.uploadedBytes / (1024f * 1024f)
        val percentage = (progress.uploadedBytes.toFloat() / progress.totalBytes * 100).toInt()

        updateState {
            copy(
                uploadingProgress = percentage.toString(),
                uploadedMegaBytes = uploadedMB.toString(),
                selectedFile = state.value.selectedFile?.copy(id = progress.reelId)
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

    private fun onUploadCompleted(errorState: ErrorState?) {
        errorState?.let {
            updateState {
                copy(uploadingState = UploadTrendsScreenState.UploadingState.FAILED)
            }
        } ?: updateState {
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
                uploadingProgress = "",
                uploadedMegaBytes = "",
                isNextButtonEnabled = false,
                selectedFile = null
            )
        }
    }

    override fun onRetryUploadClick() {
        job?.cancel()
        uploadTrend(state.value.selectedFile)
    }

    override fun onNextClick() {
        state.value.selectedFile?.let {
            sendEffect(UploadTrendsScreenEffect.NavigateToDescription(it.id))
        }
    }
}
