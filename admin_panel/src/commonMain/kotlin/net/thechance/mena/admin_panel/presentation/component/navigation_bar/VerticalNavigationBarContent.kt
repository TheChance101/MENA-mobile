package net.thechance.mena.admin_panel.presentation.component.navigation_bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalNavigationBarContent(
    items: List<VerticalNavigationItem>,
    selectedItemIndex: Int,
    onItemClick: (VerticalNavigationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.width(112.dp).padding(bottom = 34.dp)) {
        items.forEachIndexed { index, item ->
            VerticalNavigationBarItem(
                isSelected = index == selectedItemIndex,
                selectedIcon = item.selectedIcon,
                unselectedIcon = item.notSelectedIcon,
                title = item.title,
                onClick = { onItemClick(item) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}