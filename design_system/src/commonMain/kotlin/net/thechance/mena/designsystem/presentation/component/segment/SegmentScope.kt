package net.thechance.mena.designsystem.presentation.component.segment

import androidx.compose.runtime.Composable

interface SegmentScope {
    fun item(
        title: String,
        content: @Composable SegmentScope.() -> Unit
    ) {
        error("The method is not implemented")
    }
}

internal class SegmentScopeImpl : SegmentScope {
    data class SegmentItem(
        val title: String,
        val content: @Composable SegmentScope.() -> Unit
    )

    val items = mutableListOf<SegmentItem>()

    override fun item(
        title: String,
        content: @Composable SegmentScope.() -> Unit
    ) {
        items.add(SegmentItem(title, content))
    }
}