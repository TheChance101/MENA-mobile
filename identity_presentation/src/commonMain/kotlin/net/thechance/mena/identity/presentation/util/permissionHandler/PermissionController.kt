package net.thechance.mena.identity.presentation.util.permissionHandler

interface PermissionController {
    fun getPermissionState(): PermissionState
    fun openSettingPage()
    fun requestPermission()
}