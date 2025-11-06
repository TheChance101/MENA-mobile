package net.thechance.mena.identity.presentation.util.permissionHandler

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PermissionHandler(
    private val permissionController: PermissionController
) {

    fun checkPermission(): PermissionState {
        return permissionController.getPermissionState()
    }

    fun checkPermissionFlow(): Flow<PermissionState> = flow {
        while (true) {
            val permissionState = checkPermission()
            emit(permissionState)
            if (permissionState == PermissionState.GRANTED) break
            delay(PERMISSION_CHECK_FLOW_FREQUENCY)
        }
    }

    fun openSettingPage() {
        permissionController.openSettingPage()
    }

    fun requestPermission() {
        permissionController.requestPermission()
    }
}

private const val PERMISSION_CHECK_FLOW_FREQUENCY = 1000L