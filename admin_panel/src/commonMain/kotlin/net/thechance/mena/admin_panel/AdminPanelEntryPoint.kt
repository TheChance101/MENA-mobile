package net.thechance.mena.admin_panel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.component.navigation_bar.VerticalNavigationBar
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreen
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.app_name
import net.thechance.mena.admin_panel.resources.deposit
import net.thechance.mena.admin_panel.resources.dukan_management
import net.thechance.mena.admin_panel.resources.dukan_requests
import net.thechance.mena.admin_panel.resources.ic_deposit
import net.thechance.mena.admin_panel.resources.ic_deposit_selected
import net.thechance.mena.admin_panel.resources.ic_dukan
import net.thechance.mena.admin_panel.resources.ic_dukan_requests
import net.thechance.mena.admin_panel.resources.ic_dukan_requests_selected
import net.thechance.mena.admin_panel.resources.ic_dukan_selected
import net.thechance.mena.admin_panel.resources.ic_log_out
import net.thechance.mena.admin_panel.resources.ic_user_selected
import net.thechance.mena.admin_panel.resources.ic_users_mange
import net.thechance.mena.admin_panel.resources.log_out
import net.thechance.mena.admin_panel.resources.mena
import net.thechance.mena.admin_panel.resources.mena_logo
import net.thechance.mena.admin_panel.resources.users_management
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdminPanelEntryPoint() {
    var activeFeature: AdminFeature by remember { mutableStateOf(AdminFeature.USER_MANAGEMENT) }

    Row(
        Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceLow)
            .systemBarsPadding()
            .padding(bottom = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .width(112.dp)
                .fillMaxHeight()
                .background(Theme.colorScheme.background.surfaceLow),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenaLogo()

            VerticalNavigationBar(
                modifier = Modifier.weight(1f),
                initialSelectedIndex = 3
            ) {
                verticalNavigationItem(
                    selectedIcon = painterResource(Res.drawable.ic_dukan_selected),
                    notSelectedIcon = painterResource(Res.drawable.ic_dukan),
                    title = stringResource(Res.string.dukan_management),
                    entry = { activeFeature = AdminFeature.DUKAN_MANAGEMENT }
                )

                verticalNavigationItem(
                    selectedIcon = painterResource(Res.drawable.ic_dukan_requests_selected),
                    notSelectedIcon = painterResource(Res.drawable.ic_dukan_requests),
                    title = stringResource(Res.string.dukan_requests),
                    entry = { activeFeature = AdminFeature.DUKAN_REQUESTS }
                )

                verticalNavigationItem(
                    selectedIcon = painterResource(Res.drawable.ic_deposit_selected),
                    notSelectedIcon = painterResource(Res.drawable.ic_deposit),
                    title = stringResource(Res.string.deposit),
                    entry = { activeFeature = AdminFeature.DEPOSIT }
                )

                verticalNavigationItem(
                    selectedIcon = painterResource(Res.drawable.ic_user_selected),
                    notSelectedIcon = painterResource(Res.drawable.ic_users_mange),
                    title = stringResource(Res.string.users_management),
                    entry = { activeFeature = AdminFeature.USER_MANAGEMENT }
                )
                verticalNavigationItem(
                    selectedIcon = painterResource(Res.drawable.ic_log_out),
                    notSelectedIcon = painterResource(Res.drawable.ic_log_out),
                    title = "",
                    entry = { activeFeature = AdminFeature.LOG_OUT }
                )
            }

        }

        FeatureContent(activeFeature)
    }
}

@Composable
private fun RowScope.FeatureContent(activeFeature: AdminFeature) {
    Box(
        Modifier
            .weight(1f)
            .background(Theme.colorScheme.background.surfaceHigh)
    ) {
        androidx.compose.animation.Crossfade(targetState = activeFeature) { feature ->
            when (feature) {
                AdminFeature.USER_MANAGEMENT -> UsersManagementScreen()
                AdminFeature.DUKAN_MANAGEMENT -> PlaceholderScreen(stringResource(Res.string.dukan_management))
                AdminFeature.DUKAN_REQUESTS -> PlaceholderScreen(stringResource(Res.string.dukan_requests))
                AdminFeature.DEPOSIT -> PlaceholderScreen(stringResource(Res.string.deposit))
                AdminFeature.LOG_OUT -> PlaceholderScreen(stringResource(Res.string.log_out))
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = title,
            style = Theme.typography.headline.large
        )
    }
}

private enum class AdminFeature {
    USER_MANAGEMENT,
    DUKAN_MANAGEMENT,
    DUKAN_REQUESTS,
    DEPOSIT,
    LOG_OUT
}

@Composable
private fun MenaLogo() {
    Column(
        modifier = Modifier.padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.mena_logo),
            contentDescription = stringResource(Res.string.app_name),
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(Res.string.mena),
            textAlign = TextAlign.Center,
            style = Theme.typography.appName,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}