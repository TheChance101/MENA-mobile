package net.thechance.mena.identity.presentation.feature.profileFlow.imageCropper.components.imageCropper

sealed interface ImageCropperComponentEffect {
    data class SaveImage(val imageByteArray: ByteArray) : ImageCropperComponentEffect
    data class UploadAnotherImage(val imageByteArray: ByteArray) : ImageCropperComponentEffect
}