package net.thechance.mena.faith.presentation.feature.quran.bookmark.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_not_saved_book_mark
import mena.faith_presentation.generated.resources.start_tilawah
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyBookmarkState(
    title: String,
    icon: Painter,
    contentDescription: String,
    subTitle: String,
    modifier: Modifier = Modifier,
    onClickButton: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(128.dp)
        )
        Text(
            text = title,
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Theme.spacing._24, bottom = Theme.spacing._8)
        )
        Text(
            text = subTitle,
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.body.small,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Theme.spacing._24)
        )

        Button(
            modifier = Modifier.fillMaxWidth().height(48.dp)
                .clip(RoundedCornerShape(Theme.radius.md)),
            onClick = onClickButton,
            containerColor = Theme.colorScheme.primary.primary
        ) {
            Text(
                text = stringResource(Res.string.start_tilawah),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.primary.onPrimary
            )
        }
    }
}

@Preview()
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            EmptyBookmarkState(
                icon = painterResource(Res.drawable.ic_not_saved_book_mark),
                contentDescription = "Empty Bookmark",
                title = "No Bookmarks Yet",
                subTitle = "You haven't added any bookmarks. Start exploring and add your favorite items to your bookmarks."
            )
        }
    }
}
