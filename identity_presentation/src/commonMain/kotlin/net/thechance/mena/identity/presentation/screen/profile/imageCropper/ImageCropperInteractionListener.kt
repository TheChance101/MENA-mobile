package net.thechance.mena.identity.presentation.screen.profile.imageCropper

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ImageCropperInteractionListener : BaseInteractionListener {
    fun onCropImage(imageByteArray: ByteArray)

    fun onChangeImage(imageByteArray: ByteArray)

    fun onNavigateBack()
}