package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_triangle_warning
import mena.faith_presentation.generated.resources.no_mosques_found_by_keyword
import mena.faith_presentation.generated.resources.warning_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoMosquesFoundCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .padding(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
    ) {

        Icon(
            painter = painterResource(Res.drawable.ic_triangle_warning),
            contentDescription = stringResource(Res.string.warning_icon),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Theme.radius.sm))
                .background(Theme.colorScheme.background.bgWarning)
                .padding(Theme.spacing._12)
        )

        Text(
            text = message,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
        )

    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            NoMosquesFoundCard(
                message = stringResource(Res.string.no_mosques_found_by_keyword)
            )
        }
    }
}
