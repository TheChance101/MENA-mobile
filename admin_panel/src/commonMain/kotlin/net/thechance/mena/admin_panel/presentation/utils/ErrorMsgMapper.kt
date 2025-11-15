package net.thechance.mena.admin_panel.presentation.utils

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositErrorState
import net.thechance.mena.admin_panel.presentation.screen.login.LoginErrorState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.error_invalid_credentials_description
import net.thechance.mena.admin_panel.resources.error_invalid_credentials_title
import net.thechance.mena.admin_panel.resources.no_account_error_description
import net.thechance.mena.admin_panel.resources.no_account_error_title
import net.thechance.mena.admin_panel.resources.no_internet_content
import net.thechance.mena.admin_panel.resources.no_internet_title
import net.thechance.mena.admin_panel.resources.unknown_error_description
import net.thechance.mena.admin_panel.resources.unknown_error_title
import org.jetbrains.compose.resources.StringResource

fun ErrorState.getErrorSnackBarTitle(): StringResource {
    return when(this){
        is ErrorState.NoInternet -> Res.string.no_internet_title
        is ErrorState.UnknownError -> Res.string.unknown_error_title
        is LoginErrorState.InvalidCredentials -> Res.string.error_invalid_credentials_title
        is DepositErrorState.NoAccount->Res.string.no_account_error_title
        else -> Res.string.unknown_error_title
    }
}

fun ErrorState.getErrorSnackBarMsg(): StringResource {
    return when(this){
        is ErrorState.NoInternet -> Res.string.no_internet_content
        is ErrorState.UnknownError -> Res.string.unknown_error_description
        is LoginErrorState.InvalidCredentials -> Res.string.error_invalid_credentials_description
        is DepositErrorState.NoAccount->Res.string.no_account_error_description
        else -> Res.string.unknown_error_description
    }
}