package net.thechance.mena.faith.presentation.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_column_mosque
import mena.faith_presentation.generated.resources.ic_quran
import mena.faith_presentation.generated.resources.mosque_image_description
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FaithFeatureCard(
    icon: Painter,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {

        Icon(
            painter = painterResource(Res.drawable.ic_column_mosque),
            contentDescription = stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 10.dp)
                .align(Alignment.CenterEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .padding(start = Theme.spacing._12),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
            horizontalAlignment = Alignment.Start,
        ) {
            CardIcon(icon = icon, contentDescription = title)
            Text(
                text = title,
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            FaithFeatureCard(
                icon = painterResource(Res.drawable.ic_quran),
                title = "Quran Kareem",
                onClick = {}
            )
        }
    }
}
