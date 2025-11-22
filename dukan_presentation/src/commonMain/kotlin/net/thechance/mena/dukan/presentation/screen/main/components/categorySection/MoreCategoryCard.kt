package net.thechance.mena.dukan.presentation.screen.main.components.categorySection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.category_icon
import mena.dukan_presentation.generated.resources.menu_circle
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MoreCategoryCard(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surfaceLow)
                .size(60.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = stringResource(resource = Res.string.category_icon),
                tint = Theme.colorScheme.primary.primary
            )
        }

        Text(
            text = title,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = Theme.spacing._8)
                .padding(top = Theme.spacing._4)
        )
    }
}

@Preview
@Composable
private fun MoreCategoryCardPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(color = Theme.colorScheme.background.surface)
                .size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            MoreCategoryCard(
                title = "Category",
                onClick = {},
                icon = painterResource(Res.drawable.menu_circle),
            )
        }
    }
}