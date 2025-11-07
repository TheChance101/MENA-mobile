package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import org.jetbrains.compose.resources.StringResource

data class ShareQrCodeUIState(
    val showDialog: Boolean = false,
    val isLoading: Boolean = false,
    val showSnackBar: Boolean = false,
    val snackBarTitle: StringResource? = null,
    val snackBarMessage: StringResource? = null,
    val shareLinkUrl: String = "",
    val errorMessage: StringResource? = null
)