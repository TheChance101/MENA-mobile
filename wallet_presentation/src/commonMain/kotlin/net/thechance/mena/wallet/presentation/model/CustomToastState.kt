package net.thechance.mena.wallet.presentation.model

import org.jetbrains.compose.resources.StringResource

data class CustomToastState(
    val isVisible: Boolean = false,
    val messageRes: StringResource? = null,
)