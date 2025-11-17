package net.thechance.mena.admin_panel.presentation.utils

object PlatformDetector {
    private val osName = System.getProperty("os.name").lowercase()

    val isWindows: Boolean = osName.contains("win")
    val isMacOS: Boolean = osName.contains("mac") || osName.contains("darwin")
    val isLinux: Boolean = osName.contains("nux") || osName.contains("nix")

    val currentOS: OS = when {
        isMacOS -> OS.MACOS
        isWindows -> OS.WINDOWS
        isLinux -> OS.LINUX
        else -> OS.UNKNOWN
    }
}

enum class OS {
    WINDOWS, MACOS, LINUX, UNKNOWN
}
