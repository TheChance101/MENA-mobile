package net.thechance.mena.trends.presentation.shared.util.logger

import platform.Foundation.NSLog

actual fun logErrorPlatform(tag: String, message: String) {
    NSLog("[E][%@] %@", tag, message)
}