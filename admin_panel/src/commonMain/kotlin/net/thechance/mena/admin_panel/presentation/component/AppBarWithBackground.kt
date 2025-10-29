package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun AppBarWithBackground(
    title: String,
    backgroundColor: Color = Theme.colorScheme.background.surfaceLow,
    modifier: Modifier = Modifier,
    titleColor: Color = Theme.colorScheme.shadePrimary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    leadingContent: (@Composable () -> Unit)? = null,
    onLeadingClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    AppBar(
        title = title,
        titleColor = titleColor,
        contentPadding = contentPadding,
        leadingContent = leadingContent,
        onLeadingClick = onLeadingClick,
        trailingContent = trailingContent,
        modifier = modifier.background(backgroundColor)
    )
}