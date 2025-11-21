package net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.heart_icon
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_favorite_filled
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun EditorPickDukanItem(
    dukanImage: Any,
    dukanName: String,
    isFavorite: Boolean,
    onClickDukan: () -> Unit,
    onClickFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {

    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(SquircleShape(Theme.radius.lg))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onClickDukan() }
            )
    ) {

        AsyncImage(
            model = dukanImage,
            contentDescription = stringResource(Res.string.dukan_image),
            contentScale = ContentScale.Crop,
            onState = { state ->
                isError = state is AsyncImagePainter.State.Error
                isLoading = state is AsyncImagePainter.State.Loading
            },
            modifier = Modifier.fillMaxSize()
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
        Box(
            Modifier.fillMaxSize().padding(Theme.spacing._8)
        ) {
            Text(
                text = dukanName,
                style = Theme.typography.title.small,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
            )
            Crossfade(
                targetState = isFavorite,
                modifier = Modifier.align(Alignment.TopEnd)
            ) { isFavorite ->
                val favoriteIcon = if (isFavorite) Res.drawable.ic_favorite_filled
                else Res.drawable.ic_favorite

                Icon(
                    painter = painterResource(favoriteIcon),
                    contentDescription = stringResource(Res.string.heart_icon),
                    tint = Theme.colorScheme.primary.onPrimary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            onClick = onClickFavorite,
                            indication = null,
                            interactionSource = null
                        )
                        .background(color = Theme.colorScheme.primary.primary)
                        .padding(Theme.spacing._8)
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditorPickDukanItemPreview() {
    MenaTheme {
        EditorPickDukanItem(
            dukanName = "Calvin Klein store - Baghdad",
            dukanImage = "https://plus.unsplash.com/premium_photo-1664474619075-644dd191935f?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1469",
            isFavorite = true,
            onClickDukan = {},
            onClickFavorite = {}
        )
    }
}
