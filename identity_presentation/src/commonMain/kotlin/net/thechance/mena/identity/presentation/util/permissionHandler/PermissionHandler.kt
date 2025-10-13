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

    fun checkPermissionFlow(): Flow<PermissionState> {
        return flow {
            while (true) {
                val permissionState = checkPermission()
                emit(permissionState)
                delay(PERMISSION_CHECK_FLOW_FREQUENCY)
            }
        }
    }

    suspend fun providePermission() {
        try {
            permissionController.providePermission()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openSettingPage() {
        try {
            permissionController.openSettingPage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private const val PERMISSION_CHECK_FLOW_FREQUENCY = 1000L