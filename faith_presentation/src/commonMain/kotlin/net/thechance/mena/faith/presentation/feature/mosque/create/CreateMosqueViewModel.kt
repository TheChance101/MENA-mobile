package net.thechance.mena.faith.presentation.feature.mosque.create

import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal class CreateMosqueViewModel() :
    BaseViewModel<CreateMosqueUiState, CreateMosqueEffect>(
        CreateMosqueUiState()
    ), CreateMosqueInteractionListener {

    override fun onBackClicked() {
        //TODO("Not yet implemented")
    }

    override fun onEditImageMosqueClicked() {
        //TODO("Not yet implemented")
    }

    override fun onClickUploadImage(image: ImageSrc) {
        updateState {
            it.copy(
                selectedImage = image,
                isImageBeingCropped = true
            )
        }
        checkIfFormIsComplete()
    }

    override fun onAddClicked() {
        //TODO("Not yet implemented")
    }

    override fun onNameChange(name: String) {
        updateState { it.copy(name = name) }
        checkIfFormIsComplete()
    }

    override fun onAddressChanged(address: String) {
        updateState { it.copy(address = address) }
        checkIfFormIsComplete()
    }

    override fun mapPositionChanged(coordinate: Coordinate) {
        updateState { it.copy(location = coordinate) }
        checkIfFormIsComplete()
    }

    private fun checkIfFormIsComplete() {
        updateState { currentState ->
            currentState.copy(
                isButtonEnabled = currentState.name.isNotBlank() &&
                        currentState.address.isNotBlank() &&
                        currentState.location != null &&
                        currentState.croppedImage != null
            )
        }
    }
}