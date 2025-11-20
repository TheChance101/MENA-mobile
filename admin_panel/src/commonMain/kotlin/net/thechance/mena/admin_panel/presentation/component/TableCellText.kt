package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun TableCellText(
    text: String, modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = Theme.typography.body.medium,
        color = Theme.colorScheme.shadePrimary,
        softWrap = false,
        modifier = modifier
    )
}
