package net.thechance.mena.designsystem.presentation.component.bottomNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavigationBar(
    onItemClick: (BottomNavigationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavigationItem(
            selectedIcon = painterResource(Res.drawable.ic_home_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_home),
            title = "Home"
        ),
        BottomNavigationItem(
            selectedIcon = painterResource(Res.drawable.ic_dukan_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_dukan),
            title = "Dukan"
        ),
        BottomNavigationItem(
            selectedIcon = painterResource(Res.drawable.ic_trends_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_trends),
            title = "Trends"
        ),
        BottomNavigationItem(
            selectedIcon = painterResource(Res.drawable.ic_profile_selected),
            notSelectedIcon = painterResource(Res.drawable.ic_profile),
            title = "Profile"
        )
    )
    var selectedItemIndex by remember {
        mutableIntStateOf(0)
    }

    BottomNavigationBarContent(
        items = items,
        selectedItemIndex = selectedItemIndex,
        onItemClick = {
            selectedItemIndex = items.indexOf(it)
            onItemClick(it)
        },
        modifier = modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}

@Preview
@Composable
private fun PreviewBottomNavigationBar() {
    MenaTheme {
        Box(Modifier.fillMaxSize()) {
            BottomNavigationBar(
                onItemClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
            )
        }
    }
}