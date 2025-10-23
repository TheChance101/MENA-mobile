package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ImageMessageContent(
    modifier: Modifier = Modifier,
    images: List<Any> = emptyList(), // could be url: String or byteArray: ByteArray
    onImageClick: (Int) -> Unit = {},
) {
    val displayImages = images.take(4)
    val remainingCount = (images.size - 4).coerceAtLeast(0)
    val gridCells = if (displayImages.size == 2 || displayImages.size == 1) 1 else 2

    val spacing = 1.dp

    BoxWithConstraints(modifier = modifier) {
        val gridWith = maxWidth
        val gridHeight = maxHeight

        if (displayImages.size == 1) {
            ImageGridItem(
                imageUrl = displayImages[0],
                index = 0,
                onClick = onImageClick,
                modifier = Modifier.fillMaxSize()
            )
            return@BoxWithConstraints
        }

        LazyHorizontalGrid(
            rows = GridCells.Fixed(gridCells),
            modifier = Modifier
                .width(gridWith)
                .height(gridHeight),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            userScrollEnabled = false
        ) {
            when (displayImages.size) {
                2 -> {
                    items(2) { index ->
                        ImageGridItem(
                            imageUrl = displayImages[index],
                            index = index,
                            onClick = onImageClick,
                            modifier = Modifier
                                .aspectRatio(.5f)
                        )
                    }
                }

                3 -> {
                    item(span = { GridItemSpan(2) }) {
                        ImageGridItem(
                            imageUrl = displayImages[0],
                            index = 0,
                            onClick = onImageClick,
                            modifier = Modifier.fillMaxHeight().aspectRatio(.5f)
                        )
                    }

                    items(2) { index ->
                        val actualIndex = index + 1
                        ImageGridItem(
                            imageUrl = displayImages[actualIndex],
                            index = actualIndex,
                            onClick = onImageClick,
                            modifier = Modifier
                                .aspectRatio(1f)
                        )
                    }
                }

                else -> {
                    items(displayImages.size) { index ->
                        val isFourth = index == 3 && remainingCount > 0
                        ImageGridItem(
                            imageUrl = displayImages[index],
                            index = index,
                            onClick = onImageClick,
                            overlayText = if (isFourth) "+$remainingCount" else null,
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}
