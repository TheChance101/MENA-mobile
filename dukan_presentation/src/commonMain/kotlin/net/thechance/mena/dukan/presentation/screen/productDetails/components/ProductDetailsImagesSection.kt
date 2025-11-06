package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.product_image
import mena.dukan_presentation.generated.resources.product_thumbnail
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.productDetails.components.util.ShimmerBox
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsImagesSection(
    allImages: List<String>,
    selectedImageUrl: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onSecondaryImageClick: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        AnimatedContent(
            targetState = isLoading,
            label = "ImageSectionAnimation",
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
        ) { loading ->
            if (loading) {
                ProductDetailsImageShimmer(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ProductDetailsImageContent(
                    allImages = allImages,
                    selectedImageUrl = selectedImageUrl,
                    onSecondaryImageClick = onSecondaryImageClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ProductDetailsImageShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShimmerBox(
            width = Dp.Unspecified,
            height = 288.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Theme.radius.md))
        )

        Spacer(modifier = Modifier.height(Theme.spacing._16))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            repeat(4) {
                ShimmerBox(
                    width = 56.dp,
                    height = 56.dp,
                    modifier = Modifier.clip(RoundedCornerShape(Theme.radius.sm))
                )
            }
        }
    }
}

@Composable
private fun ProductDetailsImageContent(
    allImages: List<String>,
    selectedImageUrl: String,
    onSecondaryImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        ProductDetailsMainImage(
            imageUrl = selectedImageUrl,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        ProductDetailsSecondaryImages(
            images = allImages,
            selectedImageUrl = selectedImageUrl,
            onImageClick = onSecondaryImageClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun ProductDetailsMainImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(Res.string.product_image),
        modifier = modifier
            .fillMaxWidth()
            .height(288.dp)
            .clip(RoundedCornerShape(Theme.radius.md)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ProductDetailsSecondaryImages(
    images: List<String>,
    selectedImageUrl: String,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = Theme.spacing._4)
    ) {
        items(items = images) { imageUrl ->
            ProductDetailsSecondaryImageItem(
                imageUrl = imageUrl,
                isSelected = (imageUrl == selectedImageUrl),
                onClick = { onImageClick(imageUrl) }
            )
        }
    }
}

@Composable
private fun ProductDetailsSecondaryImageItem(
    imageUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(Theme.radius.sm)

    val targetBorderColor = if (isSelected) {
        Theme.colorScheme.primary.primary
    } else {
        Color.Transparent
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = targetBorderColor,
        label = "BorderColorAnimation"
    )

    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(Res.string.product_thumbnail),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(56.dp)
            .clip(shape)
            .border(1.dp, animatedBorderColor, shape)
            .clickable(onClick = onClick)
    )
}

@Preview
@Composable
private fun ProductDetailsImagesSectionPreview() {
    val images = listOf("1.jpg", "2.jpg", "3.jpg")
    MenaTheme {
        ProductDetailsImagesSection(
            allImages = images,
            selectedImageUrl = images[1],
            onSecondaryImageClick = {},
            isLoading = true
        )
    }
}