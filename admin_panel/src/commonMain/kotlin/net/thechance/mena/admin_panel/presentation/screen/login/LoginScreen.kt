package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import net.thechance.mena.admin_panel.presentation.component.AdminPanelScaffold
import net.thechance.mena.admin_panel.presentation.screen.login.component.UsernameInputField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme

@Composable
fun LoginScreen() {
    AdminPanelScaffold {
        var username by remember { mutableStateOf("") }
        Column {
            UsernameInputField(
                username = username,
                onChangeValue = { username = it }
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MenaTheme {
        LoginScreen()
    }
}