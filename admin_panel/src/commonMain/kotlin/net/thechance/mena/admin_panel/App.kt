package net.thechance.mena.admin_panel

import androidx.compose.runtime.Composable
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.MainContainerScreen
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme

@Composable
fun App() {
    MenaTheme {
        MainContainerScreen()
    }
}