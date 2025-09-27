package net.thechance.mena.dukan.presentation.component.productCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.product_image
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.productLayout.ProductUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductCardVertical(
    productUiState: ProductUiState,
    productAction: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(size = Theme.radius.sm)
            )
            .padding(Theme.spacing._4)
    ) {
        Box {
            AsyncImage(
                model = productUiState.imageUrl,
                contentDescription = stringResource(Res.string.product_image),
                modifier = Modifier
                    .width(152.dp)
                    .height(176.dp)
                    .padding(bottom = Theme.spacing._16)
                    .clip(RoundedCornerShape(Theme.radius.sm))
            )
            productAction(Modifier.align(Alignment.BottomCenter))

        }
        ProductInfo(
            name = productUiState.name,
            description = productUiState.description
        )

        Row(
            modifier = Modifier.padding(start = Theme.spacing._4, top = Theme.spacing._2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        ) {
            Image(
                painter = painterResource(Res.drawable.discount_icon),
                contentDescription = stringResource(Res.string.discount_icon),
            )

            PriceWithIcon(
                price = productUiState.price.toString(),
                iconRes = Res.drawable.silver_tc,
                contentDescription = stringResource(Res.string.koin_icon),
            )
        }
    }
}

@Preview
@Composable
private fun ProductCardVerticalPreview() {
    MenaTheme {
        ProductCardVertical(
            ProductUiState(
                id = "1",
                imageUrl = "https://www.naturephotographie.com/wp-content/uploads/2018/11/www.naturephotographie.com-garden-of-nature-1461x975.jpg",
                name = "Girls Crochet Tank Top",
                price = 39.5,
            ),
            productAction = { modifier ->
                EditProductIcon(
                    onClick = {},
                    modifier = modifier
                )
            }
        )
    }
}