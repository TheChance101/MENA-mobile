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
import net.thechance.mena.trends.presentation.shared.util.video_util.VideoUtilities
import net.thechance.mena.trends.presentation.shared.util.video_util.formatBytes
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UploadReelViewModel(
    @Provided private val reelsRepository: ReelsRepository,
    @Provided private val videoValidator: VideoMetaDataValidator,
    @Provided private val videoUtilities: VideoUtilities,
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
            block = { createVideoFile(file, readBytes) },
            onError = ::onValidationError,
            onSuccess = ::onCreateVideoFileSuccess,
            dispatcher = defaultDispatcher
        )
    }

    private suspend fun createVideoFile(
        file: FileUiState,
        readBytes: suspend () -> ByteArray
    ): FileUiState {

        val validatedFile = validateFile(
            file = file,
            readBytes = readBytes
        )

        return validatedFile
    }

    private suspend fun validateFile(
        file: FileUiState,
        readBytes: suspend () -> ByteArray
    ): FileUiState {
        videoValidator.validateSize(file.sizeInBytes)
        val bytes = readBytes()
        videoUtilities.getDuration(bytes)?.let { duration ->
            videoValidator.validateDuration(duration)
        }
        return file.copy(bytes = bytes)
    }

    private fun onCreateVideoFileSuccess(file: FileUiState) {
        updateState {
            copy(
                selectedFile = file.copy(
                    bytes = file.bytes,
                    sizeInMegaBytes = formatBytes(file.sizeInBytes)
                ),
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
                    mimeType = trendFile.mimeType,
                    size = trendFile.sizeInBytes,
                    bytes = trendFile.bytes,
                    extension = trendFile.extension
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
                uploadedBytes = progress.numberOfUploadedBytes,
                trendId = progress.reelId
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
                isNextButtonEnabled = true,
            )
        }
        extractFrame(state.value.selectedFile)
    }

    private fun extractFrame(file: FileUiState) {
        tryToExecute(
            block = {
                videoUtilities.extractVideoFrame(
                    videoData = file.bytes,
                    timeMs = 1L
                )
            },
            onSuccess = ::onExtractFrameSuccess,
            onError = ::onExtractFrameError,
            dispatcher = defaultDispatcher
        )
    }

    private fun onExtractFrameSuccess(thumbnail: ByteArray?) {
        updateState { copy( thumbnail = thumbnail ) }
    }

    private fun onExtractFrameError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    private fun uploadThumbnail(){
        tryToExecute(
            block = {
                state.value.trendId?.let {
                    reelsRepository.uploadReelThumbnail(
                        thumbnail = state.value.thumbnail ?: ByteArray(0),
                        size = state.value.thumbnail?.size?.toLong() ?: 0L,
                        name = state.value.selectedFile.name + "_thumbnail",
                        mimeType = "image/jpeg",
                        extension = "jpg",
                        id = it
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
        state.value.trendId?.let {
            sendEffect(UploadReelScreenEffect.NavigateToAddDescription(it))
        }
    }

    private fun onUploadThumbnailError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    private fun onValidationError(errorState: ErrorState) {
        updateState { copy(errorState = errorState) }
    }

    override fun onBackClick() {
        sendEffect(UploadReelScreenEffect.NavigateBack)
    }

    override fun onEditVideoClick() {
        uploadingTrendJob?.cancel()
        deleteVideo()
        updateState {
            copy(
                selectedFile = FileUiState(),
                thumbnail = null,
                uploadingTrendState = UploadReelScreenState.UploadingTrendState.IDLE,
                uploadedBytes = 0,
                isNextButtonEnabled = false,
                isNextButtonLoading = false
            )
        }
    }

    override fun onCancelUploadClick() {
        uploadingTrendJob?.cancel()
        updateState {
            copy(
                selectedFile = FileUiState(),
                thumbnail = null,
                uploadingTrendState = UploadReelScreenState.UploadingTrendState.IDLE,
                uploadedBytes = 0,
                isNextButtonEnabled = false,
                isNextButtonLoading = false
            )
        }
    }

    override fun onDeleteVideoClick() {
        uploadingTrendJob?.cancel()
        deleteVideo()
    }

    private fun deleteVideo(){
        tryToExecute(
            block = { state.value.trendId?.let { reelsRepository.deleteReelById(id = it) } },
            onSuccess = { updateState { UploadReelScreenState() } },
            onError = { errorState -> updateState { copy(errorState = errorState) } },
            dispatcher = defaultDispatcher
        )
    }

    override fun onRetryUploadClick() {
        uploadingTrendJob?.cancel()
        uploadTrend(state.value.selectedFile)
    }

    override fun onNextClick() {
        uploadThumbnail()
    }
}
