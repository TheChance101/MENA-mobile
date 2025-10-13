package net.thechance.mena.identity.presentation.util.permissionHandler

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PermissionHandler(
    private val permissionController: PermissionController
) {

    fun checkPermission(): PermissionState {
        return try {
            permissionController.getPermissionState()
        } catch (e: Exception) {
            e.printStackTrace()
            PermissionState.NOT_DETERMINED
        }
    }

    fun checkPermissionFlow(): Flow<PermissionState> = flow {
        while (true) {
            val permissionState = checkPermission()
            emit(permissionState)
            if (permissionState == PermissionState.GRANTED) break
            delay(PERMISSION_CHECK_FLOW_FREQUENCY)
        }
    }

    suspend fun providePermission() {
        permissionController.providePermission()
    }

    fun openSettingPage() {
        permissionController.openSettingPage()
    }
}

private const val PERMISSION_CHECK_FLOW_FREQUENCY = 1000L