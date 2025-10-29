package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import net.thechance.mena.admin_panel.presentation.component.AdminPanelScaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme

@Composable
fun LoginScreen() {
    AdminPanelScaffold {

    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MenaTheme {
        LoginScreen()
    }
}