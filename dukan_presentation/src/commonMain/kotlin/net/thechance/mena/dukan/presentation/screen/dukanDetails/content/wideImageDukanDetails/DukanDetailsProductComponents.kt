package net.thechance.mena.dukan.presentation.screen.dukanDetails.content.wideImageDukanDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.silver_tc
import mena.dukan_presentation.generated.resources.wide_product_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productCard.PriceWithIcon
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductCard(
    imageUrl: String,
    title: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .size(width = 160.dp, height = 240.dp)
            .clip(RoundedCornerShape(Theme.radius.sm))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(onClick = onClick, enabled = isEnabled)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(Res.string.wide_product_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(Theme.spacing._4)
                .clip(RoundedCornerShape(Theme.radius.sm))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._8)
                .padding(bottom = Theme.spacing._4),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Text(
                text = title,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Theme.spacing._12)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(Res.drawable.discount_icon),
                    contentDescription = stringResource(Res.string.discount_icon),
                    modifier = Modifier.padding(end = Theme.spacing._4)
                )
                PriceWithIcon(
                    price = price,
                    iconRes = Res.drawable.silver_tc,
                    contentDescription = stringResource(Res.string.koin_icon),
                )
            }
        }
    }
}

fun LazyGridScope.ProductsGridSection(
    state: DukanDetailsUiState,
) {
    val productCount = 6
    when (state.productsState) {
        DukanDetailsUiState.ProductsState.LOADING -> {
            items(count = productCount) {
                ProductCard(
                    imageUrl = "",
                    title = "...",
                    price = "...",
                    onClick = {},
                    isEnabled = false
                )
            }
        }

        DukanDetailsUiState.ProductsState.LOADED -> {
            items(items = state.productsShelf.items, key = { it.id }) { product ->
                ProductCard(
                    imageUrl = product.imageUrl,
                    title = product.name,
                    price = "$${product.price}",
                    onClick = {}
                )
            }
        }

        DukanDetailsUiState.ProductsState.EMPTY -> {
            item(span = { GridItemSpan(maxLineSpan) }) {}
        }
    }
}

@Preview(showBackground = true, name = "Product Card")
@Composable
private fun ProductCardPreview() {
    MenaTheme {
        ProductCard(
            imageUrl = "https://via.placeholder.com/160x240",
            title = "Fresh Red Apples",
            price = "$1.99",
            onClick = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}