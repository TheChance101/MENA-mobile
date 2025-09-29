package net.thechance.mena.trends.presentation.shared.model


enum class VideoUploadingState {
    Loading, Error, Success
}

sealed interface VideoAction {
    object Cancel : VideoAction
    object Retry : VideoAction
    object Delete : VideoAction
}
