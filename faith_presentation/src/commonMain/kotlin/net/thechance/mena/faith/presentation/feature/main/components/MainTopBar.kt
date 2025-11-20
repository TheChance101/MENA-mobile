package net.thechance.mena.faith.presentation.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.faith_title
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.location
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainTopBar(
    locationName: String,
    onLocationChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBar(
        modifier = modifier,
        title = stringResource(Res.string.faith_title),
        trailingContent = {
            Row(
                modifier = Modifier
                    .background(
                        color = Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(Theme.radius.full)
                    )
                    .padding(horizontal = Theme.spacing._8, vertical = Theme.spacing._4)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onLocationChange() }
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_location),
                    contentDescription = stringResource(Res.string.location),
                    tint = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = locationName,
                    color = Theme.colorScheme.shadePrimary,
                    style = Theme.typography.label.small,
                    maxLines = 1
                )
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            MainTopBar(
                locationName = "Palestine, Gaza",
                onLocationChange = {}
            )
        }
    }
}
