package net.thechance.mena.designsystem.presentation.component.preview

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun PreviewComponent(
    title: String,
    isScrollable: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        net.thechance.mena.designsystem.presentation.component.text.Text(
            text = title,
            style = Theme.typography.headline.small,
            color = Theme.colorScheme.shadeTertiary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
                .then(
                    if (isScrollable) Modifier.horizontalScroll(rememberScrollState())
                    else Modifier
                )
        ) {
            content()
        }
    }
}