package net.thechance.mena.dukan.presentation.component.product.productImage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_cancel
import mena.dukan_presentation.generated.resources.image_size_mb
import mena.dukan_presentation.generated.resources.upload_dukan_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.drawBorder
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun DisplayProductImage(
    image: Any,
    imageType: ImageType,
    imageSizeInMegaByte: Double = 0.0,
    onCancelClick: (Any) -> Unit,
    modifier: Modifier = Modifier,
    productImageState: ProductImageState = ProductImageState.LOADING,
    isCancelButtonEnabled: Boolean = true,
    errorMessage: String? = null,
) {
    Column(
        modifier = modifier
            .width(88.dp)
            .background(color = Transparent)
    ) {
        when (imageType) {
            ImageType.BITMAP -> {
                if (image is ImageBitmap) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedContent(
                            modifier = Modifier
                                .size(size = 88.dp),
                            targetState = productImageState,
                            label = "display product Image",
                            transitionSpec = { fadeTransitionSpec() },
                        ) { currentState ->
                            when (currentState) {
                                ProductImageState.LOADING -> LoadingContentImage(imageSize = imageSizeInMegaByte)
                                ProductImageState.SUCCESS -> SuccessContentImage(image = image)
                                ProductImageState.ERROR -> ErrorContentImage(image = image)
                            }
                        }

                        CancelImageIconButton(
                            modifier = Modifier.align(Alignment.TopEnd),
                            productImageState = productImageState,
                            onCancelClick = { onCancelClick(image) },
                            isCancelButtonEnabled = isCancelButtonEnabled
                        )
                    }

                    errorMessage?.let { error ->
                        if (productImageState == ProductImageState.ERROR) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start),
                                text = error,
                                style = Theme.typography.label.extraSmall,
                                color = Theme.colorScheme.error,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }

            ImageType.URL -> {
                if (image is String) {
                    DisplayExistingProductImage(
                        imageUrl = image,
                        onCancelClick = { onCancelClick(image) },
                        isCancelButtonEnabled = isCancelButtonEnabled
                    )
                }
            }
        }
    }
}

@Composable
private fun CancelImageIconButton(
    productImageState: ProductImageState,
    onCancelClick: () -> Unit,
    isCancelButtonEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val color = if (productImageState == ProductImageState.SUCCESS) {
        Theme.colorScheme.primary.primary
    } else {
        Theme.colorScheme.error
    }

    val tintIconColor = animateColorAsState(
        targetValue = color,
        animationSpec = tween(durationMillis = 500),
        label = "Icon Tint",
    )

    val isIconVisible = productImageState == ProductImageState.SUCCESS ||
            productImageState == ProductImageState.ERROR


    if (isIconVisible) {
        Box(
            modifier = modifier
                .size(size = 24.dp)
                .offset(x = 4.dp, y = (-4).dp)
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                    shape = SquircleShape(Theme.radius.full)
                )
                .clip(SquircleShape(Theme.radius.full))
                .clickable(
                    enabled = isCancelButtonEnabled,
                    onClick = onCancelClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_cancel),
                contentDescription = "cancel add Image",
                tint = tintIconColor.value,
            )
        }
    }
}

@Composable
private fun LoadingContentImage(imageSize: Double) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = SquircleShape(Theme.radius.md)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(resource = Res.string.image_size_mb, imageSize),
            style = Theme.typography.label.small
        )
        ProgressBar(
            modifier = Modifier
                .padding(vertical = Theme.spacing._4, horizontal = Theme.spacing._8)
                .fillMaxWidth()
                .height(height = Theme.spacing._4),
        )
    }
}

@Composable
private fun SuccessContentImage(image: ImageBitmap) {
    Box(
        modifier = Modifier.size(88.dp)
            .background(
                color = Theme.colorScheme.background.surfaceHigh,
                shape = SquircleShape(Theme.radius.md)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = SquircleShape(Theme.radius.md)),
            bitmap = image,
            contentDescription = stringResource(resource = Res.string.upload_dukan_image),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun DisplayExistingProductImage(
    imageUrl: String,
    onCancelClick: (String) -> Unit,
    isCancelButtonEnabled: Boolean
) {
    Box {
        SuccessContentImageUrl(
            imageUrl = imageUrl,
            modifier = Modifier.size(size = 88.dp)
        )
        CancelImageIconButton(
            productImageState = ProductImageState.SUCCESS,
            onCancelClick = { onCancelClick(imageUrl) },
            isCancelButtonEnabled = isCancelButtonEnabled,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun SuccessContentImageUrl(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(88.dp)
            .background(
                color = Theme.colorScheme.background.surfaceHigh,
                shape = SquircleShape(Theme.radius.md)
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(resource = Res.string.upload_dukan_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = SquircleShape(Theme.radius.md))
        )
    }
}

@Composable
private fun ErrorContentImage(image: ImageBitmap) {
    val borderColor = Theme.colorScheme.error
    val cornerRadiusValue = Theme.radius.md

    Column(
        modifier = Modifier.size(88.dp)
            .background(
                color = Theme.colorScheme.background.surfaceHigh,
                shape = SquircleShape(Theme.radius.md)
            ).drawWithContent {
                drawContent()
                drawBorder(
                    borderColor = borderColor,
                    cornerRadiusValue = cornerRadiusValue.toPx()
                )
            },
    ) {
        Image(
            modifier = Modifier.fillMaxSize().clip(SquircleShape(Theme.radius.md)),
            bitmap = image,
            contentDescription = stringResource(resource = Res.string.upload_dukan_image),
            contentScale = ContentScale.Crop
        )
    }
}

enum class ProductImageState {
    SUCCESS, LOADING, ERROR
}

enum class ImageType {
    BITMAP, URL
}

@Preview()
@Composable
private fun LoadingProductImagePreview() {
    MenaTheme {
        Box(
            modifier = Modifier.size(150.dp)
                .background(color = Theme.colorScheme.background.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            val image = ImageBitmap(100, 100)

            DisplayProductImage(
                image = image,
                imageType = ImageType.BITMAP,
                imageSizeInMegaByte = 2.0,
                productImageState = ProductImageState.LOADING,
                onCancelClick = {}
            )
        }
    }
}

@Preview()
@Composable
private fun SuccessProductImagePreview() {
    MenaTheme {
        Box(
            modifier = Modifier.size(150.dp)
                .background(color = Theme.colorScheme.background.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            val image = ImageBitmap(100, 100)

            DisplayProductImage(
                image = image,
                imageType = ImageType.BITMAP,
                imageSizeInMegaByte = 2.0,
                productImageState = ProductImageState.SUCCESS,
                onCancelClick = {}
            )
        }
    }
}

@Preview()
@Composable
private fun ErrorProductImagePreview() {
    MenaTheme {
        Box(
            modifier = Modifier.size(150.dp)
                .background(color = Theme.colorScheme.background.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            val image = ImageBitmap(100, 100)

            DisplayProductImage(
                image = image,
                imageType = ImageType.BITMAP,
                imageSizeInMegaByte = 0.0,
                productImageState = ProductImageState.ERROR,
                errorMessage = "Uploading Failed",
                onCancelClick = {}
            )
        }
    }
}