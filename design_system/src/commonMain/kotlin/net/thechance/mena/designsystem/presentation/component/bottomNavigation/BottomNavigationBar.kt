package net.thechance.mena.designsystem.presentation.component.bottomNavigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
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

@Immutable
data class BottomNavigationBarItem(
    val notSelectedIcon: Painter,
    val selectedIcon: Painter,
    val title: String
)

@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationBarItem>,
    selectedItemIndex: Int,
    onItemClick: (BottomNavigationBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier.height(74.dp)) {
        val itemWidth = maxWidth / items.size
        val indicatorWidth = itemWidth - 40.dp
        val indicatorOffset by animateDpAsState(
            targetValue = selectedItemIndex * itemWidth
        )

        Row(
            Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->
                BottomNavigationBarItem(
                    isSelected = index == selectedItemIndex,
                    selectedIcon = item.selectedIcon,
                    notSelectedIcon = item.notSelectedIcon,
                    title = item.title,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Box(
            Modifier
                .padding(horizontal = 20.dp)
                .offset(x = indicatorOffset)
                .clip(
                    RoundedCornerShape(
                        bottomEnd = Theme.radius.xs,
                        bottomStart = Theme.radius.xs
                    )
                )
                .background(Theme.colorScheme.brand.brand)
                .size(indicatorWidth, 4.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewBottomNavigationBar() {
    MenaTheme {
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
            },
            modifier = Modifier.background(Color.White)
        )
    }
}