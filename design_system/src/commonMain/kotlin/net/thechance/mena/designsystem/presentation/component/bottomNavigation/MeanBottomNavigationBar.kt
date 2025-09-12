package net.thechance.mena.designsystem.presentation.component.bottomNavigation

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_dukan
import mena.design_system.generated.resources.ic_dukan_selected
import mena.design_system.generated.resources.ic_home
import mena.design_system.generated.resources.ic_home_selected
import mena.design_system.generated.resources.ic_profile
import mena.design_system.generated.resources.ic_profile_selected
import mena.design_system.generated.resources.ic_trends
import mena.design_system.generated.resources.ic_trends_selected
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun MeanBottomNavigationBar(
    onItemClick: (BottomNavigationBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavigationBarItem(
            selectedIcon = painterResource(Res.drawable.ic_home_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_home),
            title = "Home"
        ),
        BottomNavigationBarItem(
            selectedIcon = painterResource(Res.drawable.ic_dukan_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_dukan),
            title = "Dukan"
        ),
        BottomNavigationBarItem(
            selectedIcon = painterResource(Res.drawable.ic_trends_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_trends),
            title = "Trends"
        ),
        BottomNavigationBarItem(
            selectedIcon = painterResource(Res.drawable.ic_profile_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_profile),
            title = "Profile"
        )
    )
    var selectedItemIndex by remember {
        mutableIntStateOf(0)
    }

    BottomNavigationBar(
        items = items,
        selectedItemIndex = selectedItemIndex,
        onItemClick = {
            selectedItemIndex = items.indexOf(it)
            onItemClick(it)
        },
        modifier = modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}