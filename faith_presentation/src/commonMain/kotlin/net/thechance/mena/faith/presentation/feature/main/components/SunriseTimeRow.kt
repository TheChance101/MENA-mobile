package net.thechance.mena.faith.presentation.feature.main.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun SunriseTimeRow(
    icon: Painter,
    title: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.spacing._16),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            painter = icon,
            contentDescription = null,
            tint = Theme.colorScheme.primary.primary,
            modifier = Modifier.size(20.dp)
                .padding(end = Theme.spacing._8)
        )

        Text(
            text = title,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = time,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium
        )
    }
}