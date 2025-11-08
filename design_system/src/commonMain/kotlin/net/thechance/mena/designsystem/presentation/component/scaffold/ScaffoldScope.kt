package net.thechance.mena.designsystem.presentation.component.scaffold

import androidx.compose.runtime.Composable

interface ScaffoldScope {
    fun bottomSheet(
        isVisible: Boolean,
        content: @Composable ScaffoldScope.(Boolean) -> Unit
    ) {
        error("The method is not implemented")
    }

    fun dialog(
        isVisible: Boolean,
        content: @Composable ScaffoldScope.(Boolean) -> Unit
    ) {
        error("The method is not implemented")
    }

    fun drawer(
        isVisible: Boolean,
        content: @Composable ScaffoldScope.(Boolean) -> Unit
    ) {
        error("The method is not implemented")
    }
}

internal class ScaffoldScopeImpl : ScaffoldScope {
    data class OverlayItem(
        val isVisible: Boolean,
        val content: @Composable ScaffoldScope.(Boolean) -> Unit
    )

    val items = mutableListOf<OverlayItem>()

    override fun bottomSheet(
        isVisible: Boolean,
        content: @Composable ScaffoldScope.(Boolean) -> Unit
    ) {
        items.add(OverlayItem(isVisible, content))
    }

    override fun dialog(
        isVisible: Boolean,
        content: @Composable ScaffoldScope.(Boolean) -> Unit
    ) {
        items.add(OverlayItem(isVisible, content))
    }

    override fun drawer(
        isVisible: Boolean,
        content: @Composable (ScaffoldScope.(Boolean) -> Unit)
    ) {
        items.add(OverlayItem(isVisible, content))
    }
}