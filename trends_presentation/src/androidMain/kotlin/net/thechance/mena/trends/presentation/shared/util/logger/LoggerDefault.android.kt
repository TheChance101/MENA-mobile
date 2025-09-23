package net.thechance.mena.trends.presentation.shared.util.logger

import android.util.Log

actual fun logErrorPlatform(tag: String, message: String) {
    Log.e(tag, message)
}