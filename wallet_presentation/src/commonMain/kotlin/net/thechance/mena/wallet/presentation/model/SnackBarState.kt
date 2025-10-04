package net.thechance.mena.wallet.presentation.model

import org.jetbrains.compose.resources.StringResource

data class SnackBarState(
    val isVisible: Boolean = false,
    val titleRes: StringResource? = null,
    val messageRes: StringResource? = null,
    val isSuccess: Boolean = true
)