package net.thechance.mena.admin_panel.presentation.screen.mainContainer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.MainContainerInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.MainContainerScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.app_name
import net.thechance.mena.admin_panel.resources.logout
import net.thechance.mena.admin_panel.resources.logout_bar_icon
import net.thechance.mena.admin_panel.resources.mena
import net.thechance.mena.admin_panel.resources.mena_logo
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdminPanelSideBar(
    selectedTab: MainContainerScreenState.SelectedSidebarTab,
    interactionListener: MainContainerInteractionListener,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(114.dp)
            .fillMaxHeight()
            .background(Theme.colorScheme.background.surfaceLow),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SideBarLogo(modifier = Modifier.padding(top = 32.dp, bottom = 48.dp))
        AdminSideBarTabs(
            modifier = Modifier.weight(1f),
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                interactionListener.onTabSelected(tab)
            }
        )
        Box(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { interactionListener.onLogOutClicked() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.logout_bar_icon),
                contentDescription = stringResource(Res.string.logout),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SideBarLogo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.mena_logo),
            contentDescription = stringResource(Res.string.app_name),
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(Res.string.mena),
            textAlign = TextAlign.Center,
            style = Theme.typography.appName.copy(fontSize = 14.sp),
            color = Theme.colorScheme.shadePrimary
        )
    }
}

