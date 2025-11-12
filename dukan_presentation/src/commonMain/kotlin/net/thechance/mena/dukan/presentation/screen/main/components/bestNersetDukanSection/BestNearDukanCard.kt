package net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_image
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BestNearDukanCard(
    dukanName: String,
    imageUrl: Any,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp).clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = { onClick() }
        )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(Res.string.dukan_image),
            modifier = Modifier
                .size(size = 60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = dukanName,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            modifier = Modifier.padding(top = Theme.spacing._4)
        )
    }
}

@Preview
@Composable
private fun BestNearDukanCardPreview() {
    MenaTheme {
        BestNearDukanCard(
            dukanName = "DeFacto",
            imageUrl = "https://www.ascenciamalls.com/media/3lncwwzl/the-faceshop-2-_1.jpg?anchor=center&mode=crop&width=784&height=650&rnd=133027033116700000",
            onClick = {}
        )
    }
}