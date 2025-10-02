package net.thechance.mena.trends.presentation.shared.model


sealed interface VideoAction {
    object Cancel : VideoAction
    object Retry : VideoAction
    object Delete : VideoAction
}
