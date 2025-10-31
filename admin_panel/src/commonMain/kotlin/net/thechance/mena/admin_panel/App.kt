package net.thechance.mena.admin_panel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import net.thechance.mena.admin_panel.presentation.screen.ExampleScreen
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme

@Composable
@Preview
fun App(){
    MenaTheme {
        ExampleScreen()
    }
}