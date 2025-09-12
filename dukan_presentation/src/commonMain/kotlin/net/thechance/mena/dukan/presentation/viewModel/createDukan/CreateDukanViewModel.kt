package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.presentation.base.BaseViewModel

class CreateDukanViewModel :
    BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    override fun onClickUploadImage() {
        emitEffect(CreateDukanEffect.NavigateToImageCropScreen)
    }

    override fun onClickEditImage() {
        emitEffect(CreateDukanEffect.NavigateToImageCropScreen)
    }

    override fun onCLickNext() {
        TODO("Not yet implemented")
    }

    override fun onZoomInClicked() {
        TODO("Not yet implemented")
    }

    override fun onZoomOutClicked() {
        TODO("Not yet implemented")
    }

    override fun onResetClicked() {
        TODO("Not yet implemented")
    }

    override fun onUploadAnotherImageClicked() {
        TODO("Not yet implemented")
    }

    fun onImageSelected(image: Int) {
        updateState {
            copy(
                selectedImageUri = image,
                isNextButtonEnabled = true
            )
        }
    }
}
