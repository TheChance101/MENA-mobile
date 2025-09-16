package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep

class CreateDukanViewModel(
    private val dukanRepository: DukanRepository
) :
    BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    override fun onButtonClicked() {
        if (state.value.currentStep != CreateDukanUiState.CreateDukanStep.SELECT_STYLE) {
            onCLickNext()
        } else {
            onCreateClicked()
        }
    }

    override fun onBackClicked() {
        val current = state.value.currentStep
        if (current == CreateDukanUiState.CreateDukanStep.BASIC_INFORMATION) {
            // maybe do nothing or exit flow
        } else {
            updateState {
                copy(currentStep = previousStep(current))
            }
        }
        updateNextButtonEnableState()
    }


    override fun onColorClicked(color: Long) = updateState { copy(selectedColor = color) }

    override fun onStyleClicked(style: StyleUiState) = updateState { copy(selectedStyle = style) }

    fun updateCreateButtonState() {
        val state = state.value
        if (state.selectedStyle != null && state.selectedColor != null) {
            updateState {
                copy(isButtonEnabled = true)
            }
        } else {
            updateState {
                copy(isButtonEnabled = false)
            }
        }
    }

    private fun getDukanColors() {
        tryToExecute(
            block = { dukanRepository.getDukanColors() },
            onSuccess = {::updateScreenStateWithColors },
            onError = {::handleError},
        )
    }

    private fun getDukanStyle() {
        tryToExecute(
            block = { dukanRepository.getDukanStyles().map { style -> style.toUiState() } },
            onSuccess = {::updateScreenStateWithStyles },
            onError = {::handleError },
        )
    }

    private fun updateScreenStateWithStyles(dukanStyles:List<StyleUiState>) {
        updateState {
            copy(dukanStyles =dukanStyles )
        }
    }

    private fun updateScreenStateWithColors(dukanColors:List<Long>) {
        updateState {
            copy(dukanColors =dukanColors )
        }
    }

    private fun handleError(errorMessage: String){
        updateState {
            copy(errorMessage=errorMessage)
        }
    }

    private fun onCreateClicked() {
        TODO("Not yet implemented")
    }

    override fun onClickUploadImage(
        image: ImageSrc
    ) {
        updateState {
            copy(
                selectedImage = image,
                isEditIconVisible = false,
                isImageBeingCropped = true
            )
        }
        updateNextButtonEnableState()
    }

    override fun onClickEditImage() {
        emitEffect(CreateDukanEffect.NavigateToImageCropScreen)
    }

    override fun onCLickNext() {
        val current = state.value.currentStep
        updateState {
            copy(currentStep = nextStep(current))
        }
    }

    override fun onImageCrop(image: ImageBitmap) {
        updateState {
            copy(
                croppedImage = image,
                selectedImage = null,
                isEditIconVisible = true,
                isImageBeingCropped = false
            )
        }
        updateNextButtonEnableState()
    }

    override fun onCancelCrop() {
        updateState {
            copy(
                selectedImage = null,
                isImageBeingCropped = false
            )
        }
        updateNextButtonEnableState()
    }

    private fun nextStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> CreateDukanStep.SELECT_IMAGE
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.SELECT_LOCATION
            CreateDukanStep.SELECT_LOCATION -> CreateDukanStep.SELECT_STYLE
            CreateDukanStep.SELECT_STYLE -> step
        }

    private fun previousStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> step
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.BASIC_INFORMATION
            CreateDukanStep.SELECT_LOCATION -> CreateDukanStep.SELECT_IMAGE
            CreateDukanStep.SELECT_STYLE -> CreateDukanStep.SELECT_LOCATION
        }

    private fun updateNextButtonEnableState() {
        val state = state.value
        val isNextButtonEnabled = when (state.currentStep) {
            CreateDukanStep.BASIC_INFORMATION -> true
            CreateDukanStep.SELECT_IMAGE -> state.croppedImage != null
            CreateDukanStep.SELECT_LOCATION -> true
            CreateDukanStep.SELECT_STYLE -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    private companion object {
        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 4f
        private const val ZOOM_STEP = 0.25f
    }
}