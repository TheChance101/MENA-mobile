package net.thechance.mena.core_chat.presentation.utils

interface SettingsOpener {
    fun openSettings()
}

class SettingsOpenerImpl : SettingsOpener {
    override fun openSettings() {
        openAppSettings()
    }
}

expect fun openAppSettings()