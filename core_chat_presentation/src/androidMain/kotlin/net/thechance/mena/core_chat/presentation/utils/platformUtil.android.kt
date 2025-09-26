package net.thechance.mena.core_chat.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import org.koin.core.context.GlobalContext

actual fun openAppSettings() {
    val context = GlobalContext.get().get<Context>()
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}