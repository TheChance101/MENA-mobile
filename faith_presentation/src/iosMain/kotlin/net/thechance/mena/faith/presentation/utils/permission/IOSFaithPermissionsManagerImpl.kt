package net.thechance.mena.faith.presentation.utils.permission

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

class IOSFaithPermissionsManagerImpl : FaithPermissionsManager {

    override suspend fun checkPermission(permission: PermissionType): PermissionState {
        return when (permission) {
            PermissionType.NOTIFICATIONS -> checkNotificationPermission()
            else -> PermissionState(granted = true)
        }
    }

    override suspend fun requestPermission(permission: PermissionType) {
        when (permission) {
            PermissionType.NOTIFICATIONS -> requestNotificationPermission()
        }
    }

    private suspend fun checkNotificationPermission(): PermissionState =
        suspendCancellableCoroutine { cont ->
            UNUserNotificationCenter.currentNotificationCenter()
                .getNotificationSettingsWithCompletionHandler { settings ->
                    cont.resume(PermissionState(settings?.authorizationStatus == 2L))
                }
        }

    private suspend fun requestNotificationPermission(): PermissionState =
        suspendCancellableCoroutine { cont ->
            UNUserNotificationCenter.currentNotificationCenter()
                .requestAuthorizationWithOptions(
                    UNAuthorizationOptionAlert or UNAuthorizationOptionSound
                ) { granted, _ ->
                    cont.resume(PermissionState(granted))
                }
        }
}
