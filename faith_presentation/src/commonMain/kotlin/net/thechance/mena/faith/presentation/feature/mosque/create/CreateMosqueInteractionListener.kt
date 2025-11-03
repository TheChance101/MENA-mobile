package net.thechance.mena.faith.presentation.feature.mosque.create

import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal interface CreateMosqueInteractionListener {
    fun onBackClicked()
    fun onEditImageMosqueClicked()
    fun onClickUploadImage(image: ImageSrc)
    fun onNameChange(name: String)
    fun onAddressChanged(address: String)
    fun mapPositionChanged(coordinate: Coordinate)

}