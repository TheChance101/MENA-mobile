package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep

class CreateDukanViewModel :
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

    override fun onResetClicked() {
        updateState {
            copy(
                zoomFactor = MIN_ZOOM,
                isZoomOutEnabled = false
            )
        }
    }

    private fun onCreateClicked() {
        TODO("Not yet implemented")
    }

    override fun onClickUploadImage() {
        emitEffect(CreateDukanEffect.NavigateToImageCropScreen)
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

    fun onImageCroppedAndSaved(croppedUri: String) {
        updateState {
            copy(
                savedImageUri = croppedUri,
                isNextButtonEnabled = true
            )
        }
    }

    override fun onSaveClicked() {
        updateState {
            copy(
                isNextButtonEnabled = true,
                isEditIconVisible = true
            )
        }
    }

    override fun onZoomInClicked() {
        val current = state.value.zoomFactor
        val newZoom = (current + ZOOM_STEP).coerceAtMost(MAX_ZOOM)
        updateState {
            copy(
                zoomFactor = newZoom,
                isZoomOutEnabled = newZoom > MIN_ZOOM
            )
        }
    }

    override fun onZoomOutClicked() {
        val current = state.value.zoomFactor
        val newZoom = (current - ZOOM_STEP).coerceAtLeast(MIN_ZOOM)
        updateState {
            copy(
                zoomFactor = newZoom,
                isZoomOutEnabled = newZoom > MIN_ZOOM
            )
        }
    }

    override fun onUploadAnotherImageClicked() {}
    private fun nextStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> CreateDukanStep.SELECT_IMAGE
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.SELECT_LOCATION
            CreateDukanStep.SELECT_LOCATION -> CreateDukanStep.SELECT_STYLE
            CreateDukanStep.SELECT_STYLE -> step
            CreateDukanStep.CROP_IMAGE -> CreateDukanStep.CROP_IMAGE
        }

    private fun previousStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> step
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.BASIC_INFORMATION
            CreateDukanStep.SELECT_LOCATION -> CreateDukanStep.SELECT_IMAGE
            CreateDukanStep.SELECT_STYLE -> CreateDukanStep.SELECT_LOCATION
            CreateDukanStep.CROP_IMAGE -> CreateDukanStep.CROP_IMAGE
        }

    private fun updateNextButtonEnableState() {
        val state = state.value
        val isNextButtonEnabled = when (state.currentStep) {
            CreateDukanStep.BASIC_INFORMATION -> true
            CreateDukanStep.SELECT_IMAGE -> state.savedImageUri != null
            CreateDukanStep.SELECT_LOCATION -> true
            CreateDukanStep.SELECT_STYLE -> true
            CreateDukanStep.CROP_IMAGE -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    private companion object {
        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 4f
        private const val ZOOM_STEP = 0.25f
    }
}