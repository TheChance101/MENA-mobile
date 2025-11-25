package net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.downloaded_sur
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_setting
import mena.faith_presentation.generated.resources.reciters_settings
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DownloadedSurAppBar(
    onRecitersSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    AppBar(
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                tint = Theme.colorScheme.primary.primary,
                contentDescription = stringResource(Res.string.arrow_left),
            )
        },
        onLeadingClick = onBackClick,
        title = stringResource(Res.string.downloaded_sur),
        contentPadding = PaddingValues(
            vertical = Theme.spacing._8,
            horizontal = Theme.spacing._16,
        ),
        trailingContent = {
            AppBarOptionContainer(
                onClick = onRecitersSettingsClick,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_setting),
                    tint = Theme.colorScheme.primary.primary,
                    contentDescription = stringResource(Res.string.reciters_settings),
                )
            }
        },
    )
}

@Preview
@Composable
private fun PreviewDownloadedSurScreen() {
    MenaTheme {
        QuranTheme {
            DownloadedSurAppBar(
                {},
                {},
            )
        }
    }
}
