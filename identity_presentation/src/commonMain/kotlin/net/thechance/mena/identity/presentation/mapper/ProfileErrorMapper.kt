package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.presentation.base.errorState.ProfileErrorState
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_camera_permission_required
import mena.identity_presentation.generated.resources.error_first_name_required
import mena.identity_presentation.generated.resources.error_last_name_required
import mena.identity_presentation.generated.resources.error_no_network
import mena.identity_presentation.generated.resources.error_password_mismatch
import mena.identity_presentation.generated.resources.error_something_went_wrong
import mena.identity_presentation.generated.resources.error_username_required
import org.jetbrains.compose.resources.StringResource

internal fun mapProfileErrorToMessage(error: ProfileErrorState): StringResource {
    return when (error) {
        ProfileErrorState.UsernameRequired -> Res.string.error_username_required
        ProfileErrorState.FirstNameRequired -> Res.string.error_first_name_required
        ProfileErrorState.LastNameRequired -> Res.string.error_last_name_required
        ProfileErrorState.PasswordMismatch -> Res.string.error_password_mismatch
        ProfileErrorState.CameraPermissionRequired -> Res.string.error_camera_permission_required
        ProfileErrorState.NoNetwork -> Res.string.error_no_network
        is ProfileErrorState.SomethingWentWrong -> Res.string.error_something_went_wrong
    }
}