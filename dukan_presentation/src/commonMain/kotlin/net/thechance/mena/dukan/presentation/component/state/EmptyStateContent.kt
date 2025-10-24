package net.thechance.mena.dukan.presentation.component.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.empty_shelf
import mena.dukan_presentation.generated.resources.shelf_empty_body
import mena.dukan_presentation.generated.resources.shelf_empty_title
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyStateContent(
    image: DrawableResource,
    title: StringResource,
    body: StringResource,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ImageWithTextContainer(
            foregroundImageRes = image,
            header = {
                Text(
                    text = stringResource(title),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    textAlign = TextAlign.Center
                )
            },
            bodyText = stringResource(body)
        )
    }
}

@Preview
@Composable
private fun EmptyStateContentPreview() {
    MenaTheme {
        EmptyStateContent(
            image = Res.drawable.empty_shelf,
            title = Res.string.shelf_empty_title,
            body = Res.string.shelf_empty_body
        )
    }
}
