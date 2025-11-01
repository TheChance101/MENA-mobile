package net.thechance.mena.admin_panel.presentation.screen.login

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.error_invalid_credentials_description
import net.thechance.mena.admin_panel.resources.error_invalid_credentials_title
import net.thechance.mena.admin_panel.resources.no_internet_content
import net.thechance.mena.admin_panel.resources.no_internet_title
import net.thechance.mena.admin_panel.resources.unknown_error_description
import net.thechance.mena.admin_panel.resources.unknown_error_title
import org.jetbrains.compose.resources.StringResource

fun ErrorState.getErrorSnackBarTitle(): StringResource {
    return when(this){
        ErrorState.NoInternet -> Res.string.no_internet_title
        ErrorState.InvalidCredentials -> Res.string.error_invalid_credentials_title
        ErrorState.UnknownError -> Res.string.unknown_error_title
    }
}

fun ErrorState.getErrorSnackBarMsg(): StringResource {
    return when(this){
        ErrorState.NoInternet -> Res.string.no_internet_content
        ErrorState.InvalidCredentials -> Res.string.error_invalid_credentials_description
        ErrorState.UnknownError -> Res.string.unknown_error_description
    }
}