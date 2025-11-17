package net.thechance.mena.admin_panel.presentation.utils
expect object PlatformDetector {
    val currentOS: OS
    val isDesktop: Boolean
    val isMobile: Boolean
}

enum class OS {
    WINDOWS, MACOS, LINUX, ANDROID, IOS, UNKNOWN
}

