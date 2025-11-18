package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.image_1_1
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.productImage.DisplayProductImage
import net.thechance.mena.dukan.presentation.component.product.productImage.ImageType
import net.thechance.mena.dukan.presentation.component.product.productImage.ProductImageState
import net.thechance.mena.dukan.presentation.component.product.productImage.UploadProductImage
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageSection(
    images: List<CreateProductUiState.ProductImageUi>,
    isUploadingImageEnabled: Boolean,
    isCancelImageEnabled: Boolean,
    onUploadImageClick: (image: ImageFile) -> Unit,
    onCancelImageClick: (image: ImageBitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val lazyListImageState = rememberLazyListState()
        var previousSize by remember { mutableStateOf(images.size) }
        LaunchedEffect(images.size) {
            if (images.size > previousSize) {
                lazyListImageState.animateScrollToItem(images.size)
            }
            previousSize = images.size
        }
        Text(
            text = stringResource(Res.string.image_1_1),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
        LazyRow(
            modifier = Modifier
                .padding(bottom = Theme.spacing._16)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(
                start = Theme.spacing._16,
                top = Theme.spacing._4,
                end = Theme.spacing._16
            ),
            reverseLayout = true,
            state = lazyListImageState
        ) {
            items(
                items = images,
                key = { it.id },
                contentType = { "Product Images" }
            ) { image ->
                DisplayProductImage(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(easing = FastOutSlowInEasing),
                        fadeOutSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                        placementSpec = tween(easing = LinearOutSlowInEasing)
                    ),
                    image = image.image,
                    imageType = ImageType.BITMAP,
                    imageSizeInMegaByte = image.imageSizeInMegaByte,
                    productImageState = image.imageState,
                    onCancelClick = { onCancelImageClick(image.image) },
                    isCancelButtonEnabled = isCancelImageEnabled,
                    errorMessage = image.errorMessage
                )
            }
            item(
                key = "Upload Product Image Container"
            ) {
                UploadProductImage(
                    modifier = Modifier.size(88.dp),
                    onUploadImageClick = onUploadImageClick,
                    isUploadingImageEnabled = isUploadingImageEnabled
                )
            }
        }
    }
}

@Composable
fun ImageSection(
    images: List<EditProductUiState.ProductImageUi>,
    existingImageUrls: List<String>,
    isUploadingImageEnabled: Boolean,
    isCancelImageEnabled: Boolean,
    onUploadImageClick: (image: ImageFile) -> Unit,
    onCancelImageClick: (image: ImageBitmap) -> Unit,
    onCancelImageUrlClick: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val lazyListImageState = rememberLazyListState()
        val totalImagesCount = images.size + existingImageUrls.size
        var previousSize by remember { mutableStateOf(totalImagesCount) }

        LaunchedEffect(totalImagesCount) {
            if (totalImagesCount > previousSize) {
                lazyListImageState.animateScrollToItem(totalImagesCount)
            }
            previousSize = totalImagesCount
        }

        Text(
            text = stringResource(Res.string.image_1_1),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )

        LazyRow(
            modifier = Modifier
                .height(108.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(
                start = Theme.spacing._16,
                top = Theme.spacing._4,
                end = Theme.spacing._16
            ),
            reverseLayout = true,
            state = lazyListImageState
        ) {
            items(
                items = existingImageUrls,
                key = { it },
                contentType = { "Product Image URL" }
            ) { imageUrl ->
                DisplayProductImage(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(easing = FastOutSlowInEasing),
                        fadeOutSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                        placementSpec = tween(easing = LinearOutSlowInEasing)
                    ),
                    image = imageUrl,
                    imageType = ImageType.URL,
                    onCancelClick = { onCancelImageUrlClick(imageUrl) },
                    isCancelButtonEnabled = isCancelImageEnabled
                )
            }

            items(
                items = images,
                key = { it.id },
                contentType = { "Product Images" }
            ) { image ->
                DisplayProductImage(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(easing = FastOutSlowInEasing),
                        fadeOutSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                        placementSpec = tween(easing = LinearOutSlowInEasing)
                    ),
                    image = image.image,
                    imageType = ImageType.BITMAP,
                    imageSizeInMegaByte = image.imageSizeInMegaByte,
                    productImageState = image.imageState,
                    onCancelClick = { onCancelImageClick(image.image) },
                    isCancelButtonEnabled = isCancelImageEnabled,
                    errorMessage = image.errorMessage
                )
            }

            item(
                key = "Upload Product Image Container"
            ) {
                UploadProductImage(
                    modifier = Modifier.size(88.dp),
                    onUploadImageClick = onUploadImageClick,
                    isUploadingImageEnabled = isUploadingImageEnabled
                )
            }
        }
    }
}

@Preview
@Composable
private fun ImageSectionPreview() {
    MenaTheme {
        ImageSection(
            images = listOf(
                CreateProductUiState.ProductImageUi(
                    id = 1L,
                    image = ImageBitmap(100, 100),
                    imageSizeInMegaByte = 0.8,
                    imageState = ProductImageState.LOADING
                ),
                CreateProductUiState.ProductImageUi(
                    id = 2L,
                    image = ImageBitmap(100, 100),
                    imageSizeInMegaByte = 0.5,
                    imageState = ProductImageState.SUCCESS
                ),
                CreateProductUiState.ProductImageUi(
                    id = 3L,
                    image = ImageBitmap(100, 100),
                    imageSizeInMegaByte = 0.5,
                    imageState = ProductImageState.SUCCESS
                )
            ),
            isUploadingImageEnabled = true,
            isCancelImageEnabled = true,
            onUploadImageClick = {},
            onCancelImageClick = {}
        )

    }
}
