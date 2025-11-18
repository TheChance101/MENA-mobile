package net.thechance.mena.admin_panel

import androidx.compose.runtime.Composable
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.MainContainerScreen
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.util.AppTheme

@Composable
fun App() {
    MenaTheme(
        appTheme  = AppTheme.LIGHT.name
    ){
        MainContainerScreen()
    }
}