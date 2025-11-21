package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.animation.skeletonLoading
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun DukanCard(
    title: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(SquircleShape(Theme.radius.lg))
            .background(Theme.colorScheme.background.surfaceHigh)
            .clickable(
                enabled = !isLoading,
                interactionSource = null,
                indication = null,
                onClick = onClick
            )
    ) {
        if (!isLoading) {
            DukanCardContent(
                title = title,
                imageUrl = imageUrl,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Composable
private fun BoxScope.DukanCardContent(
    title: String,
    imageUrl: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    AsyncImage(
        model = imageUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        onState = { state ->
            isError = state is AsyncImagePainter.State.Error
            isLoading = state is AsyncImagePainter.State.Loading
        },
        modifier = Modifier
            .fillMaxSize()
            .skeletonLoading(isLoading = isLoading)
    )
    if (isError || isLoading) {
        Icon(
            painter = painterResource(Res.drawable.ic_no_image_loaded),
            contentDescription = null,
            tint = Theme.colorScheme.primary.primary,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center),
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
    )

    FavoriteIcon(
        isFavorite = isFavorite,
        onClick = onFavoriteClick,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(Theme.spacing._8)
    )

    Text(
        text = title,
        color = Color.White,
        style = Theme.typography.title.small,
        maxLines = 1,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(Theme.spacing._8)
    )
}

@Preview
@Composable
private fun DukanCardLoadingPreview() {
    MenaTheme {
        DukanCard(
            title = "Dukan",
            imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400",
            onClick = {},
            isFavorite = false,
            isLoading = true
        )
    }
}

@Preview
@Composable
private fun DukanCardFavoritePreview() {
    MenaTheme {
        DukanCard(
            title = "Dukan",
            imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400",
            onClick = {},
            isFavorite = true
        )
    }
}

@Preview
@Composable
private fun DukanCardEmptyImagePreview() {
    MenaTheme {
        DukanCard(
            title = "Dukan Without Image",
            imageUrl = "",
            isFavorite = false,
            onClick = {}
        )
    }
}
