package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.UsersLoadingIndicator
import net.thechance.mena.admin_panel.presentation.utils.PaginationTrigger
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.img_silver
import net.thechance.mena.admin_panel.resources.product_img
import net.thechance.mena.admin_panel.resources.silver_img
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun productsList(
    products: List<Product>,
    onNextPageRequested: () -> Unit,
    isProductLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    PaginationTrigger(
        list = products,
        listState = listState,
        buffer = 5,
        loadNextItems = onNextPageRequested
    )

    when {
        isProductLoading && products.isEmpty() -> {
            Box(
                modifier = modifier.height(600.dp),
                contentAlignment = Alignment.Center
            ) {
                UsersLoadingIndicator()
            }
        }

        products.isEmpty() -> {
            EmptyShelfScreen(modifier = Modifier.fillMaxSize())
        }

        else -> {
            LazyColumn(
                modifier = modifier.height(600.dp),
                state = listState,
            ) {
                items(products) { product ->
                    ProductCard(
                        modifier = Modifier.padding(bottom = 8.dp),
                        product = product
                    )
                }
                if (isProductLoading) {
                    item {
                        UsersLoadingIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KamelImage(
            modifier = Modifier
                .padding(end = 8.dp, start = 16.dp)
                .size(96.dp)
                .clip(RoundedCornerShape(Theme.radius.sm)),
            resource = { asyncPainterResource(data = product.imageUrls.first()) },
            contentDescription = stringResource(Res.string.product_img),
            contentScale = ContentScale.Crop,
            onLoading = { LoadingImage(Modifier.align(Alignment.Center)) },
            onFailure = { ImagePlaceHolder(Modifier.fillMaxSize().align(Alignment.Center)) },
            animationSpec = tween(durationMillis = 300)
        )
        Column {
            Text(
                text = product.name,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = product.description,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary
            )
            Spacer(modifier = Modifier.weight(1f))
            ProductPrice(discountedPrice = product.discountedPrice, price = product.price)
        }
    }
}

@Composable
private fun ProductPrice(
    discountedPrice: Double?,
    price: Double,
) {
    Row {
        discountedPrice?.let {
            Text(
                text = it.toString(),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary
            )
        }
        Text(
            modifier = Modifier.padding(start = 2.dp),
            text = price.toString(),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(Res.drawable.img_silver),
            contentDescription = stringResource(Res.string.silver_img)
        )
    }
}