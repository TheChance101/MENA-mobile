package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.lifecycle.viewModelScope
import com.attafitamim.krop.core.images.ImageSrc
import kotlinx.coroutines.launch
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate
import net.thechance.mena.faith.presentation.feature.mosque.shared.SharedImageViewModel

internal class CreateMosqueViewModel(
    val sharedImageViewModel: SharedImageViewModel
) :
    BaseViewModel<CreateMosqueUiState, CreateMosqueEffect>(
        CreateMosqueUiState()
    ), CreateMosqueInteractionListener {

    init {
        observeCroppedImage()
    }

    private fun observeCroppedImage() {
        viewModelScope.launch {
            sharedImageViewModel.croppedImage.collect { image ->
                image?.let {
                    updateState { it.copy(croppedImage = image) }
                }
            }
        }
    }

    override fun onBackClicked() {
        //TODO("Not yet implemented")
    }

    override fun onEditImageMosqueClicked() {
        //TODO("Not yet implemented")
    }

    override fun onClickUploadImage(image: ImageSrc) {
        sharedImageViewModel.updateImageSrc(image)
        sendEffect(CreateMosqueEffect.NavigateToUploadImageRoute)
    }

    override fun onNameChange(name: String) {
        //TODO("Not yet implemented")
    }

    override fun onAddressChanged(address: String) {
        //TODO("Not yet implemented")
    }

    override fun mapPositionChanged(coordinate: Coordinate) {
        //TODO("Not yet implemented")
    }
}