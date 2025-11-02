package net.thechance.mena.admin_panel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreen
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import kotlin.uuid.ExperimentalUuidApi

@Preview
@Composable
fun App(){
    MenaTheme {
        UsersManagementScreen()
    }
}