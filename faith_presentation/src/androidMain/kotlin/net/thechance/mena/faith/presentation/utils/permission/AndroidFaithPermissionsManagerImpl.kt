package net.thechance.mena.faith.presentation.utils.permission

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AndroidFaithPermissionsManagerImpl(
    private val context: Context,
) : FaithPermissionsManager {
    companion object {
        private lateinit var activity: ComponentActivity
        lateinit var launcher: ActivityResultLauncher<Array<String>>


        fun init(activity: ComponentActivity) {
            this.activity = activity
            launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {}
        }
    }

    private fun mapPermission(permission: PermissionType): String = when (permission) {
        PermissionType.NOTIFICATIONS -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                ""
            }
        }
    }

    override suspend fun checkPermission(permission: PermissionType): PermissionState {
        val androidPermission = mapPermission(permission)
        val granted = ContextCompat.checkSelfPermission(
            context, androidPermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val rationale = if (androidPermission.isNotEmpty()) {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermission)
        } else {
            false
        }
        return PermissionState(granted, rationale)
    }

    override suspend fun requestPermission(permission: PermissionType) {
        val androidPermission = mapPermission(permission)
        if (androidPermission.isEmpty()) return
        launcher.launch(listOf(androidPermission).toTypedArray())
    }
}