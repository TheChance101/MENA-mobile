package net.thechance.mena.admin_panel.presentation.component.navigation_bar

import androidx.compose.ui.graphics.painter.Painter

interface VerticalNavigationScope {
    fun verticalNavigationItem(
        notSelectedIcon: Painter,
        selectedIcon: Painter,
        title: String,
        entry: () -> Unit,
    )
}

data class VerticalNavigationItem(
    val notSelectedIcon: Painter,
    val selectedIcon: Painter,
    val title: String,
    val entry: () -> Unit,
)

internal class VerticalNavigationScopeImpl : VerticalNavigationScope {
    private val _items = mutableListOf<VerticalNavigationItem>()
    val items: List<VerticalNavigationItem> get() = _items

    internal fun clear() {
        _items.clear()
    }

    override fun verticalNavigationItem(
        notSelectedIcon: Painter,
        selectedIcon: Painter,
        title: String,
        entry: () -> Unit,
    ) {
        _items.add(VerticalNavigationItem(notSelectedIcon, selectedIcon, title, entry))
    }
}