package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalDarkTheme = staticCompositionLocalOf { false }