package net.thechance.mena.trends.presentation.screen.upload_trend

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.thechance.mena.trends.domain.entity.UploadReelProgress
import net.thechance.mena.trends.domain.repository.UploadReelsRepository
import net.thechance.mena.trends.domain.usecase.validation.VideoMetaDataValidator
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.formatBytes
import net.thechance.mena.trends.presentation.shared.util.formatDuration
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

    override fun onBackClick() {
        sendEffect(UploadTrendsScreenEffect.NavigateToDescription())
    }

    override fun onUploadVideoClick() {
        // TODO:
    }

    override fun onRetrieveVideo(file: FileUiState, readBytes: suspend () -> ByteArray) {
        var bytes = ByteArray(0)
        tryToExecute(
            block = {
                validator.validateSize(file.sizeInBytes).also {
                    bytes = readBytes()
                    getVideoDuration(bytes)?.let { validator.validateDuration(it) }
                }
            },
            onError = ::onValidationError,
            onSuccess = { onValidationSuccess(file, bytes) }
        )
    }

    private fun onValidationError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    private fun onValidationSuccess(file: FileUiState, bytes: ByteArray) {
        viewModelScope.launch {
            val duration = getVideoDuration(bytes) ?: 0L
            updateState {
                copy(
                    selectedFile = FileUiState(
                        name = file.name,
                        extension = file.extension,
                        duration = formatDuration(duration),
                        sizeInBytes = file.sizeInBytes,
                        size = formatBytes(file.sizeInBytes),
                        bytes = bytes
                    )
                )
            }
        }
        uploadTrend(state.value.selectedFile)
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
            onEach = ::onEachFlow,
            onError = ::onUploadError,
            onComplete = ::onUploadCompleted,
            scope = viewModelScope
        )
    }

    private fun onUploadStarted() {
        updateState { copy(uploadingState = UploadTrendsScreenState.UploadingState.UPLOADING) }
    }

    private fun onEachFlow(progress: UploadReelProgress) {
        val uploadedMB = progress.uploadedBytes / (1024f * 1024f)
        val percentage = (progress.uploadedBytes.toFloat() / progress.totalBytes * 100).toInt()

        updateState {
            copy(
                uploadingProgress = percentage.toString(),
                uploadedMegaBytes = uploadedMB.toString()
            )
        }
    }

    private fun onUploadError(errorState: ErrorState) {  // TODO: use error state
        updateState {
            copy(
                uploadingState = UploadTrendsScreenState.UploadingState.FAILED,
                isNextButtonEnabled = false
            )
        }
    }

    private fun onUploadCompleted(errorState: ErrorState?) {
        errorState?.let {
            updateState {
                copy(
                    uploadingState = UploadTrendsScreenState.UploadingState.FAILED,
                    isNextButtonEnabled = false
                )
            }
        } ?: updateState {
            copy(
                uploadingState = UploadTrendsScreenState.UploadingState.SUCCESS,
                isNextButtonEnabled = true
            )
        }
    }

    override fun onEditVideoClick() {
        job?.cancel()
        // TODO("pick new file")
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
                selectedFile = FileUiState()
            )
        }
    }

    override fun onRetryUploadClick() {
        uploadTrend(state.value.selectedFile)
    }

    override fun onNextClick() {
        // TODO("Not yet implemented")
    }
}
