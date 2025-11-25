package net.thechance.mena.faith.presentation.utils.permission

interface FaithPermissionsManager {
    suspend fun checkPermission(permission: PermissionType): PermissionState
    suspend fun requestPermission(permission: PermissionType)
}
