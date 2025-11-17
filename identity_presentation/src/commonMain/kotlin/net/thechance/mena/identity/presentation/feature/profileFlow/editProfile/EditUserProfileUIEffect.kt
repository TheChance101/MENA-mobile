package net.thechance.mena.identity.presentation.feature.profileFlow.editProfile

sealed interface EditUserProfileUIEffect {
    object NavigateBackToProfile : EditUserProfileUIEffect
    data class NavigateToCropScreen(
        val imageKey: String,
        val onResult: (String) -> Unit
    ) : EditUserProfileUIEffect
}