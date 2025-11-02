package net.thechance.mena.admin_panel.presentation.model

data class SnackBarState(
    val isVisible: Boolean = false,
    val title: String? = null,
    val message: String? = null,
    val isSuccess: Boolean = true
)