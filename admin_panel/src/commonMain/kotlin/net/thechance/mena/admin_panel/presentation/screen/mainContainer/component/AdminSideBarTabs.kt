package net.thechance.mena.admin_panel.presentation.screen.mainContainer.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.MainContainerScreenState
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdminSideBarTabs(
    selectedTab: MainContainerScreenState.SelectedSidebarTab,
    onTabSelected: (tab: MainContainerScreenState.SelectedSidebarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val adminPanelSidePBarTabs = remember {
        listOf(
            MainContainerScreenState.SelectedSidebarTab.DUKAN_MANAGEMENT,
            MainContainerScreenState.SelectedSidebarTab.DUKAN_REQUEST,
            MainContainerScreenState.SelectedSidebarTab.DEPOSIT,
            MainContainerScreenState.SelectedSidebarTab.USERS_MANAGEMENT
        )
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 8.dp)
    ) {
        adminPanelSidePBarTabs.forEach { tab ->
            AdminSidebarItem(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = selectedTab != tab) { onTabSelected(tab) },
                title = stringResource(tab.title),
                selectedIcon = painterResource(tab.selectedIconRes),
                notSelectedIcon = painterResource(tab.unSelectedIconRes),
                isSelected = selectedTab == tab
            )
        }
    }
}

@Composable
private fun AdminSidebarItem(
    title: String,
    selectedIcon: Painter,
    notSelectedIcon: Painter,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val titleColor =
        if (isSelected) Theme.colorScheme.shadeSecondary else Theme.colorScheme.brand.brand
    val animatedTitleColor by animateColorAsState(
        targetValue = titleColor
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crossfade(targetState = isSelected) { selected ->
            val icon = if (selected) selectedIcon else notSelectedIcon
            Icon(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = title,
            style = Theme.typography.label.small,
            color = animatedTitleColor,
            textAlign = TextAlign.Center
        )
    }
}