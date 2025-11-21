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
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_sunrise
import mena.faith_presentation.generated.resources.sunrise_time_label
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SunriseTimeRow(
    icon: Painter,
    title: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            painter = icon,
            contentDescription = null,
            tint = Theme.colorScheme.primary.primary,
            modifier = Modifier.size(24.dp)
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

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            SunriseTimeRow(
                icon = painterResource(Res.drawable.ic_sunrise),
                title = stringResource(Res.string.sunrise_time_label),
                time = "06:55 PM"
            )
        }
    }
}
