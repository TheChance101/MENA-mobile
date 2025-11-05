package net.thechance.mena.admin_panel.presentation.screen.user_managements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.users_management
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserManagementScreen(modifier: Modifier = Modifier) {
    Column(
        modifier,
    ) {
        Row(
            modifier = Modifier.align(Alignment.Start)
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.users_management),
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary
            )
        }

        Text(
            text = "Users Managements Screen",
            style = Theme.typography.title.large,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}