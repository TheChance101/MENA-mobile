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
        //TODO("Not yet implemented")
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