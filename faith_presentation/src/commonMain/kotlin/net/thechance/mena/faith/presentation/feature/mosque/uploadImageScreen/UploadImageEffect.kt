package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

sealed interface UploadImageEffect {
    data object NavigateBack : UploadImageEffect
}