package net.thechance.mena.faith.presentation.feature.mosque.create

import com.attafitamim.krop.core.images.ImageSrc

internal interface CreateMosqueInteractionListener {
    fun onBackClick()
    fun onClickUploadImage(image: ImageSrc)
    fun onAddClick()
    fun onNameChange(name: String)
    fun onAddressChange(address: String)
    fun onClickMap()
    fun onEditMarkerClick()
}