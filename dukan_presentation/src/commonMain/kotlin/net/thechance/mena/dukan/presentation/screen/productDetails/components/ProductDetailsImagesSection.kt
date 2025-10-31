package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.product_image
import mena.dukan_presentation.generated.resources.product_thumbnail
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsImagesSection(
    allImages: List<String>,
    selectedImageUrl: String,
    onSecondaryImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .height(320.dp)
    ) {
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
        modifier = modifier.height(64.dp),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(images) { imageUrl ->
            val isSelected = (imageUrl == selectedImageUrl)
            val shape = RoundedCornerShape(Theme.radius.sm)
            val borderColor = if (isSelected) {
                Theme.colorScheme.primary.primary
            } else {
                Color.Transparent
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(Res.string.product_thumbnail),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(shape)
                    .border(1.dp, borderColor, shape)
                    .clickable { onImageClick(imageUrl) }
            )
        }
    }
}

@Preview
@Composable
private fun ProductDetailsImagesSectionPreview() {
    val images = listOf("1.jpg", "2.jpg", "3.jpg")
    MenaTheme {
        ProductDetailsImagesSection(
            allImages = images,
            selectedImageUrl = images[1],
            onSecondaryImageClick = {}
        )
    }
}
