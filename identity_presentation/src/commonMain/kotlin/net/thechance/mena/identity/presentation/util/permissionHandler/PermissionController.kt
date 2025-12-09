package net.thechance.mena.identity.presentation.util.permissionHandler

interface PermissionController {
    fun getPermissionState(): PermissionState
    fun openSettingPage()
    suspend fun requestPermission(): PermissionState
}