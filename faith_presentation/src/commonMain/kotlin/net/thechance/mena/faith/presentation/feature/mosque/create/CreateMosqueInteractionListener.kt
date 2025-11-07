package net.thechance.mena.faith.presentation.feature.mosque.create

import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal interface CreateMosqueInteractionListener {
    fun onBackClick()
    fun onEditImageMosqueClick()
    fun onClickUploadImage(image: ImageSrc)
    fun onAddClick()
    fun onNameChange(name: String)
    fun onAddressChange(address: String)
    fun mapPositionChange(coordinate: Coordinate)
}