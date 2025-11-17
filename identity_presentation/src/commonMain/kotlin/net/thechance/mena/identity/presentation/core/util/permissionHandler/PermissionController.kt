package net.thechance.mena.identity.presentation.core.util.permissionHandler

interface PermissionController {
    fun getPermissionState(): PermissionState
    fun openSettingPage()
    fun requestPermission()
}