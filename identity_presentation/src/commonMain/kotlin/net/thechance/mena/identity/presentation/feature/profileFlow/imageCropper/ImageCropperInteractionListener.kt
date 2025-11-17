package net.thechance.mena.identity.presentation.feature.profileFlow.imageCropper

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface ImageCropperInteractionListener : BaseInteractionListener {
    fun onCropImage(imageByteArray: ByteArray)
    fun onChangeImage(imageByteArray: ByteArray)
    fun onNavigateBack()
    fun onDismissSnackBar()
}