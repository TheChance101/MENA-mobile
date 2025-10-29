package net.thechance.mena.admin_panel.presentation.component.navigation_bar

import androidx.compose.ui.graphics.painter.Painter

interface VerticalNavigationScope {
    fun verticalNavigationItem(
        notSelectedIcon: Painter,
        selectedIcon: Painter,
        title: String,
        entry: () -> Unit,
    ) {
        error(" i think here is an error")
    }
}

data class VerticalNavigationItem(
    val notSelectedIcon: Painter,
    val selectedIcon: Painter,
    val title: String,
    val entry: () -> Unit,
)

internal class VerticalNavigationScopeImpl : VerticalNavigationScope {
    val items = mutableListOf<VerticalNavigationItem>()

    override fun verticalNavigationItem(
        notSelectedIcon: Painter,
        selectedIcon: Painter,
        title: String,
        entry: () -> Unit,
    ) {
        items.add(VerticalNavigationItem(notSelectedIcon, selectedIcon, title, entry))
    }
}