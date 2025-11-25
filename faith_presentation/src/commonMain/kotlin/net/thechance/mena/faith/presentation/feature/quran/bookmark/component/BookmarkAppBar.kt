package net.thechance.mena.faith.presentation.feature.quran.bookmark.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.bookmarks
import mena.faith_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BookmarkAppBar(onBackClick: () -> Unit) {
    AppBar(
        title = stringResource(Res.string.bookmarks),
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._8,
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                tint = Theme.colorScheme.primary.primary,
                contentDescription = stringResource(Res.string.arrow_left),
            )
        },
        onLeadingClick = onBackClick,
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            BookmarkAppBar(
                onBackClick = {}
            )
        }
    }
}
