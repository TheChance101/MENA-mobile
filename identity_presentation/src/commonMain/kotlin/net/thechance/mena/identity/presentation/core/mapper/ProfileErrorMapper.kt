package net.thechance.mena.identity.presentation.core.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_camera_permission_required
import mena.identity_presentation.generated.resources.error_first_name_required
import mena.identity_presentation.generated.resources.error_last_name_required
import mena.identity_presentation.generated.resources.error_no_network
import mena.identity_presentation.generated.resources.error_password_mismatch
import mena.identity_presentation.generated.resources.error_something_went_wrong
import mena.identity_presentation.generated.resources.error_username_required
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState.CameraPermissionRequired
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState.FirstNameRequired
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState.LastNameRequired
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState.NoNetwork
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState.PasswordMismatch
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState.SomethingWentWrong
import net.thechance.mena.identity.presentation.core.base.errorState.ProfileErrorState.UsernameRequired
import org.jetbrains.compose.resources.StringResource

internal fun mapProfileErrorToMessage(error: ProfileErrorState): StringResource {
    return when (error) {
        UsernameRequired -> Res.string.error_username_required
        FirstNameRequired -> Res.string.error_first_name_required
        LastNameRequired -> Res.string.error_last_name_required
        PasswordMismatch -> Res.string.error_password_mismatch
        CameraPermissionRequired -> Res.string.error_camera_permission_required
        NoNetwork -> Res.string.error_no_network
        is SomethingWentWrong -> Res.string.error_something_went_wrong
    }
}