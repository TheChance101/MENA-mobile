package net.thechance.mena.dukan.presentation.component.product

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.out_of_stock
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape


@Composable
fun ProductCard(
    productName: String,
    productImageUrl: String,
    productDescription: String,
    productPrice: Double,
    isOutOfStock: Boolean,
    modifier: Modifier = Modifier,
    productCardBackground: Color? = null,
    isDukanStyleNoImage: Boolean = false,
    productImageBackground: Color = Theme.colorScheme.background.surfaceLow,
    productAction: @Composable () -> Unit = {},
    onProductClick: () -> Unit = {}
) {
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val imageCorner by animateDpAsState(targetValue = if (isDukanStyleNoImage) Theme.radius.lg else Theme.radius.sm)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = productCardBackground ?: Color.Transparent,
                shape = SquircleShape(Theme.radius.md)
            )
            .height(104.dp)
            .clip(SquircleShape(Theme.radius.md))
            .clickable(onClick = onProductClick, indication = null, interactionSource = null)
            .padding(Theme.spacing._4),
    ) {
        Box(
            modifier = Modifier.background(
                color = productImageBackground,
                shape = SquircleShape(imageCorner)
            )
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(SquircleShape(imageCorner))
                    .background(productImageBackground)
            ) {
                AsyncImage(
                    model = productImageUrl,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    onState = {
                        isError = it is AsyncImagePainter.State.Error
                        isLoading = it is AsyncImagePainter.State.Loading

                    },
                    contentScale = ContentScale.Crop
                )

                if (isOutOfStock && isDukanStyleNoImage) {
                    OutOfStockLabelNoImageDukan()
                }
            }
            if (isOutOfStock && !isDukanStyleNoImage) {
                OutOfStockLabel()
            }

            if (isError || isLoading) {
                Image(
                    painter = painterResource(Res.drawable.ic_no_image_loaded),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = Theme.spacing._8,
                    top = Theme.spacing._4,
                    end = Theme.spacing._4
                ),
        ) {
            ProductInfo(
                name = productName,
                description = productDescription
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Theme.spacing._4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriceWithIcon(
                    price = productPrice.toString(),
                    iconRes = Res.drawable.silver_tc,
                    contentDescription = stringResource(Res.string.koin_icon),
                )
                Spacer(modifier = Modifier.weight(1f))

                productAction()
            }
        }
    }
}

@Composable
private fun BoxScope.OutOfStockLabelNoImageDukan() {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .rotate(
                if (isRtl) -45f else 45f
            )
            .offset(y = (-33).dp)
            .background(
                color = Theme.colorScheme.brand.brand,
                shape = RectangleShape
            ).padding(
                horizontal = Theme.spacing._16,
                vertical = Theme.spacing._2
            )
    ) {
        Text(
            text = stringResource(Res.string.out_of_stock),
            style = Theme.typography.label.extraSmall,
            color = Theme.colorScheme.primary.onPrimary,
        )
    }
}

@Composable
private fun BoxScope.OutOfStockLabel() {
    Box(
        modifier = Modifier
            .clip(SquircleShape(topEnd = Theme.radius.sm, topStart = Theme.radius.sm))
            .background(color = Theme.colorScheme.brand.brand)
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .align(Alignment.BottomCenter)

    ) {
        Text(
            text = stringResource(Res.string.out_of_stock),
            style = Theme.typography.label.extraSmall,
            color = Theme.colorScheme.primary.onPrimary
        )
    }
}

@Preview
@Composable
private fun ProductCardPreview() {
    MenaTheme(
        appTheme = AppTheme.LIGHT.name
    ) {
        ProductCard(
            productName = "Girls Crochet Tank Top",
            productImageUrl = "https://calvinklein.scene7.com/is/image/CalvinKlein/LX001376_100_alternate1?wid=1728&qlt=80%2C0&resMode=sharp2&op_usm=0.9%2C1.0%2C8%2C0&iccEmbed=0&fmt=webp",
            productDescription = "Girls Crochet Tank Top description text here for this product",
            productPrice = 39.5,
            productCardBackground = Theme.colorScheme.background.surfaceLow,
            productAction = { EditProductIcon(onClick = {}) },
            modifier = Modifier.padding(Theme.spacing._12),
            isOutOfStock = true,

            )
    }
}