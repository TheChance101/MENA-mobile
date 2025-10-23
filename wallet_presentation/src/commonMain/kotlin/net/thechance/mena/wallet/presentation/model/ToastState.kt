package net.thechance.mena.wallet.presentation.model

import org.jetbrains.compose.resources.StringResource

data class ToastState(
    val isVisible: Boolean = false,
    val messageRes: StringResource? = null,
)