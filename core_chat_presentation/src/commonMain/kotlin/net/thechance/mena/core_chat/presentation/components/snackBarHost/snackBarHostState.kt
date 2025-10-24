package net.thechance.mena.core_chat.presentation.components.snackBarHost

import net.thechance.mena.core_chat.presentation.utils.UiText


data class SnackBarHostState (
    val isVisible: Boolean = false,
    val snackBarData: SnackBarData = SnackBarData(
        title = UiText.DynamicString(),
        message = UiText.DynamicString()
    )
)

data class SnackBarData(
    val title: UiText,
    val message: UiText,
    val isError: Boolean = true,
    val duration: Long = 2500
)