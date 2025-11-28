package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import mena.dukan_presentation.generated.resources.wide_product_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.ProductPrice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape


@Composable
fun BestSellingItem(
    imageUrl: String,
    title: String,
    basePrice: Double,
    finalPrice: Double,
    isOutOfStock: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    productAction: @Composable () -> Unit,
) {
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    Box(
        modifier = modifier
            .width(112.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(SquircleShape(Theme.radius.sm))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(onClick = onClick, indication = null, interactionSource = null)
                .padding(Theme.spacing._4)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(Res.string.wide_product_image),
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        isError = state is AsyncImagePainter.State.Error
                        isLoading = state is AsyncImagePainter.State.Loading
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                        .clip(SquircleShape(Theme.radius.sm))
                )

                if (isError || isLoading) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_no_image_loaded),
                        contentDescription = null,
                        tint = Theme.colorScheme.primary.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center),
                    )
                }

                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 12.dp)
                ) {
                    productAction()
                }
            }

            Text(
                text = title,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 2,
                minLines = 2,
                modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
            )

            ProductPrice(basePrice, finalPrice)

        }
        if (isOutOfStock)
            OutOfStockLabel()
    }
}