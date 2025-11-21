package net.thechance.mena.identity.presentation.screen.editProfile

import org.jetbrains.compose.resources.StringResource

sealed interface EditUserProfileUIEffect {
    data class NavigateBackToProfile(val successStringResource: StringResource? = null) : EditUserProfileUIEffect

    data class NavigateToCropScreen(
        val imageKey: String,
        val onResult: (String) -> Unit
    ) : EditUserProfileUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : EditUserProfileUIEffect
}