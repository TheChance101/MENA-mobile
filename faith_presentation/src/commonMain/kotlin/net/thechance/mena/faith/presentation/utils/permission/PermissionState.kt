package net.thechance.mena.faith.presentation.utils.permission

data class PermissionState(
    val granted: Boolean,
    val shouldShowRationale: Boolean = false
)