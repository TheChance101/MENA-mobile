package net.thechance.mena.admin_panel

import androidx.compose.runtime.Composable
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreen
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun App(){
    MenaTheme {
        UsersManagementScreen()

    }
}