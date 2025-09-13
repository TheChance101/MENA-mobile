package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class CreateDukanViewModel(
) : BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    override fun onButtonClicked() {
        if (state.value.currentStep != SELECT_STYLE_INDEX) {
            onNextClicked()
        } else {
            onCreateClicked()
        }
    }

    override fun onBackClicked() {
        val s = state.value
        if (s.currentStep > BASIC_INFORMATION_INDEX) {
            updateState { copy(currentStep = currentStep - 1) }
        }
        updateNextButtonEnableState()
    }

    override fun onClickUploadImage() {
        emitEffect(CreateDukanEffect.NavigateToImageCropScreen)
    }

    override fun onClickEditImage() {
        emitEffect(CreateDukanEffect.NavigateToImageCropScreen)
    }

    override fun onCLickNext() {
        onNextClicked()
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
        val fakeUri = "file:///tmp/cropped_image.jpg"
        onImageCroppedAndSaved(fakeUri)
    }

    override fun onZoomInClicked() {}

    override fun onZoomOutClicked() {}

    override fun onResetClicked() {}

    override fun onUploadAnotherImageClicked() {}

    private fun onCreateClicked() {}

    private fun onNextClicked() {
        updateState { copy(currentStep = currentStep + 1) }
        updateNextButtonEnableState()
        emitEffect(CreateDukanEffect.NavigateNext)
    }

    private fun updateNextButtonEnableState() {
        val state = state.value
        val isNextButtonEnabled = when (state.currentStep) {
            BASIC_INFORMATION_INDEX -> true
            SELECT_IMAGE_INDEX -> state.savedImageUri != null
            SELECT_LOCATION_INDEX -> true
            SELECT_STYLE_INDEX -> true
            else -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    companion object {
        const val MAX_STEPS = 4
        const val BASIC_INFORMATION_INDEX = 0
        const val SELECT_IMAGE_INDEX = 1
        const val SELECT_LOCATION_INDEX = 2
        const val SELECT_STYLE_INDEX = 3
    }
}