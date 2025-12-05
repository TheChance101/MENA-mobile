package net.thechance.mena.identity.presentation.util.permissionHandler

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.presentation.util.getKoinPermissionController
import org.koin.core.component.KoinComponent
import kotlin.coroutines.resume

class PermissionHandler : KoinComponent {

    fun checkPermission(permission: Permissions): PermissionState {
        return getKoinPermissionController(permission).getPermissionState()
    }

    fun checkPermissionFlow(permission: Permissions): Flow<PermissionState> = flow {
        while (true) {
            val permissionState = checkPermission(permission)
            emit(permissionState)
            if (permissionState == PermissionState.GRANTED) break
            delay(PERMISSION_CHECK_FLOW_FREQUENCY)
        }
    }

    fun openSettingPage(permission: Permissions) {
        getKoinPermissionController(permission).openSettingPage()
    }

    suspend fun requestPermission(permission: Permissions) {
            getKoinPermissionController(permission).requestPermission()
        }
    }

private const val PERMISSION_CHECK_FLOW_FREQUENCY = 1000L