package net.thechance.mena.identity.presentation.util.permissions.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import net.thechance.mena.identity.domain.exception.CannotOpenSettingsException
import androidx.core.net.toUri

internal fun Context.openPage(
    action: String,
    newData: Uri? = null,
    onError: (Exception) -> Unit,
) {
    try {
        val intent = Intent(action).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            newData?.let { data = it }
        }
        startActivity(intent)
    } catch (e: Exception) {
        onError(e)
    }
}

internal fun Context.openAppSettingsPage() {
    openPage(
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        newData = "package:$packageName".toUri(),
        onError = { throw CannotOpenSettingsException() }
    )
}