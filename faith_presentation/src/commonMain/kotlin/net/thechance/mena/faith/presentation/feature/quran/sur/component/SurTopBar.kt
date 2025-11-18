package net.thechance.mena.faith.presentation.feature.quran.sur.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.back_icon
import mena.faith_presentation.generated.resources.bookmark_icon
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_bookmark
import mena.faith_presentation.generated.resources.ic_search
import mena.faith_presentation.generated.resources.quran
import mena.faith_presentation.generated.resources.search_icon
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
fun SurTopbar(
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppBar(
        title = stringResource(resource = Res.string.quran),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Theme.spacing._8),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(resource = Res.string.back_icon),
                tint = Theme.colorScheme.primary.primary,
                modifier = Modifier.size(20.dp)
            )

        },
        onLeadingClick = onBackClick,
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
            ) {
                AppBarIcon(
                    iconRes = painterResource(Res.drawable.ic_search),
                    contentDescription = stringResource(resource = Res.string.search_icon),
                    onClick = onSearchClick
                )

                AppBarIcon(
                    iconRes = painterResource(Res.drawable.ic_bookmark),
                    contentDescription = stringResource(resource = Res.string.bookmark_icon),
                    onClick = onBookmarkClick
                )
            }
        }
    )
}

@Composable
private fun AppBarIcon(
    iconRes: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBarOptionContainer(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = iconRes,
            contentDescription = contentDescription,
            tint = Theme.colorScheme.primary.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            SurTopbar(
                onBackClick = {},
                onBookmarkClick = {},
                onSearchClick = {}
            )
        }
    }
}
