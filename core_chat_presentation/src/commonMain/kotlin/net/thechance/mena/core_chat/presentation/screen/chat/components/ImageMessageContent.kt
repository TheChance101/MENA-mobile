package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.placeholder
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageMessageContent(
    modifier: Modifier = Modifier,
    images: List<String> = emptyList(),
    onImageClick: (Int) -> Unit = {},
) {
    val displayImages = images.take(4)
    val remainingCount = (images.size - 4).coerceAtLeast(0)

    BoxWithConstraints(
        modifier = modifier
    ) {
        val gridCells = if (displayImages.size == 1) 1 else 2
        val gridSize = maxWidth
        val spacing = 1.dp
        val cornerRadius = Theme.spacing._16

        val cellWidth = if (displayImages.size == 1) gridSize else (gridSize - spacing) / 2

        val cellHeight = if (displayImages.size == 1 || displayImages.size == 2) gridSize
        else (gridSize - spacing) / 2

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridCells),
            modifier = Modifier
                .width(gridSize)
                .height(gridSize)
                .clip(RoundedCornerShape(cornerRadius)),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            userScrollEnabled = false
        ) {
            items(displayImages.size) { index ->
                val isFourth = index == 3 && remainingCount > 0
                Box(
                    modifier = Modifier
                        .size(width = cellWidth, height = cellHeight)
                        .clickable { onImageClick(index) },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = displayImages[index],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(Res.drawable.placeholder),
                        error = painterResource(Res.drawable.placeholder),
                        colorFilter = if (isFourth) ColorFilter.tint(
                            color = Color.Black.copy(alpha = .7f),
                            blendMode = BlendMode.Darken
                        ) else null,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isFourth) {
                        Text(
                            text = "+$remainingCount",
                            color = Theme.colorScheme.primary.onPrimary,
                            style = Theme.typography.label.large
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview()
private fun `preview more than 4 images`() {

    MenaTheme {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.spacing._16))
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._4)
        ) {
            ImageMessageContent(
                modifier = Modifier,
                images = List(6) { "" }
            )
        }
    }
}

@Composable
@Preview()
private fun `preview 4 images`() {

    MenaTheme {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.spacing._16))
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._4)
        ) {
            ImageMessageContent(
                modifier = Modifier,
                images = List(4) { "" }
            )
        }
    }
}

@Composable
@Preview()
private fun `preview 3 images`() {

    MenaTheme {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.spacing._16))
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._4)
        ) {
            ImageMessageContent(
                modifier = Modifier,
                images = List(3) { "" }
            )
        }
    }
}

@Composable
@Preview()
private fun `preview 2 images`() {

    MenaTheme {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.spacing._16))
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._4)
        ) {
            ImageMessageContent(
                modifier = Modifier,
                images = List(2) { "" }
            )
        }
    }
}


@Composable
@Preview()
private fun `preview 1 image`() {

    MenaTheme {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.spacing._16))
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._4)
        ) {
            ImageMessageContent(
                modifier = Modifier,
                images = List(1) { "" }
            )
        }
    }
}

