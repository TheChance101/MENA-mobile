package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productImage.DisplayProductImage
import net.thechance.mena.dukan.presentation.component.productImage.UploadProductImage
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState.ProductImageUi
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.imageSection(
    images: List<ProductImageUi>,
    isUploadingImageEnabled: Boolean,
    isCancelImageEnabled: Boolean,
    onUploadImageClick: (image: ImageFile) -> Unit,
    onCancelImageClick: (image: ImageBitmap) -> Unit,
) {
    item {
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
            style = Theme.typography.title.medium,
            modifier = Modifier
                .padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16)
        )
        LazyRow(
            modifier = Modifier
                .padding(bottom = Theme.spacing._32 + Theme.spacing._16 + Theme.spacing._2)
                .height(108.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
            contentPadding = PaddingValues(
                start = Theme.spacing._16,
                top = Theme.spacing._4,
                end = Theme.spacing._8
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
                        fadeInSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        fadeOutSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                        placementSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                    image = image.image,
                    imageSizeInMegaByte = image.imageSizeInMegaByte,
                    productImageState = image.imageState,
                    onCancelClick = onCancelImageClick,
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
