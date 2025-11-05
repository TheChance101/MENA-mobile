package net.thechance.mena.admin_panel.presentation.screen.user_managements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun UserManagementScreen() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(start = 114.dp)
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(state.currentTab.title),
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary
            )
        }

        Text(
            text = "Users Managements Screen",
            style = Theme.typography.title.large
        )
    }
}