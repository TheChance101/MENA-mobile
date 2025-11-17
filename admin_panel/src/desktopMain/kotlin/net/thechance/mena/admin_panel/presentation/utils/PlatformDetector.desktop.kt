package net.thechance.mena.admin_panel.presentation.utils

actual object PlatformDetector {
    private val osName = System.getProperty("os.name").lowercase()

    private val isWindows: Boolean = osName.contains("win")
    private val isMacOS: Boolean = osName.contains("mac") || osName.contains("darwin")
    private val isLinux: Boolean = osName.contains("nux") || osName.contains("nix")

    actual val currentOS: OS = when {
        isMacOS -> OS.MACOS
        isWindows -> OS.WINDOWS
        isLinux -> OS.LINUX
        else -> OS.UNKNOWN
    }

    actual val isDesktop: Boolean = true
    actual val isMobile: Boolean = false
}