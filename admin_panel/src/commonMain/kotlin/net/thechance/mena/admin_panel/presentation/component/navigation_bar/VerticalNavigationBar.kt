package net.thechance.mena.admin_panel.presentation.component.navigation_bar

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun VerticalNavigationBar(
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 0,
    content: @Composable VerticalNavigationScope.() -> Unit = {},
) {
    val scope = remember { VerticalNavigationScopeImpl() }
    scope.clear()
    scope.content()

    var selectedItemIndex by remember(initialSelectedIndex) {
        mutableIntStateOf(initialSelectedIndex)
    }

    VerticalNavigationBarContent(
        items = scope.items,
        selectedItemIndex = selectedItemIndex,
        onItemClick = {
            selectedItemIndex = scope.items.indexOf(it)
            scope.items[selectedItemIndex].entry.invoke()
        },
        modifier = modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}