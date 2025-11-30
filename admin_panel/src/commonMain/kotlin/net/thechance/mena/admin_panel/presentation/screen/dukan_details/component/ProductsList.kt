package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.presentation.component.AdminPanelContentLoading
import net.thechance.mena.admin_panel.presentation.component.StatePlaceholder
import net.thechance.mena.admin_panel.presentation.utils.PaginationTrigger
import net.thechance.mena.admin_panel.presentation.utils.formatAmount
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_img
import net.thechance.mena.admin_panel.resources.empty_shelf_img
import net.thechance.mena.admin_panel.resources.empty_shelf_title
import net.thechance.mena.admin_panel.resources.ic_dukan_placholder
import net.thechance.mena.admin_panel.resources.silver_img
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductsList(
    products: List<Product>,
    onNextPageRequested: () -> Unit,
    isProductLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(products.isEmpty()) {
        listState.scrollToItem(0)
    }

    PaginationTrigger(
        listState = listState,
        buffer = 5,
        loadNextItems = onNextPageRequested
    )

    when {
        isProductLoading && products.isEmpty() -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(600.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AdminPanelContentLoading()
            }
        }

        products.isEmpty() -> { EmptyProductsState() }

        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth(),
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
                        AdminPanelContentLoading()
                    }
                }
            }
        }
    }
}

internal fun LazyListScope.lazyProductsList(
    products: List<Product>,
    isProductLoading: Boolean,
    modifier: Modifier = Modifier
) {
    when {
        isProductLoading && products.isEmpty() -> {
            item {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(Theme.colorScheme.background.surfaceLow)
                        .padding(vertical = 12.dp)
                        .height(600.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AdminPanelContentLoading()
                }
            }
        }

        products.isEmpty() -> { item { EmptyProductsState() } }

        else -> {
            items(products) { product ->
                ProductCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Theme.colorScheme.background.surfaceLow)
                        .padding(bottom = 8.dp),
                    product = product
                )
            }
            if (isProductLoading) {
                item {
                    AdminPanelContentLoading()
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
        modifier = modifier
            .padding(4.dp)
            .height(96.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(end = 4.dp, start = 16.dp)
                .size(96.dp)
                .clip(RoundedCornerShape(Theme.radius.sm)),
            model = product.imageUrls.first(),
            contentDescription = stringResource(Res.string.dukan_img),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(Res.drawable.ic_dukan_placholder),
            error = painterResource(Res.drawable.ic_dukan_placholder),
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp)
        ) {
            Text(
                text = product.name,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = product.description,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            ProductPrice(basePrice = product.basePrice, finalPrice = product.finalPrice)
        }
    }
}

@Composable
private fun ProductPrice(
    basePrice: Double,
    finalPrice: Double,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        if (basePrice != finalPrice) {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = formatAmount(basePrice),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                textDecoration = TextDecoration.LineThrough
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 2.dp)
                .alignByBaseline(),
            text = formatAmount(finalPrice),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Image(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(20.dp)
                .alignByBaseline(),
            painter = painterResource(Res.drawable.silver_img),
            contentDescription = stringResource(Res.string.silver_img)
        )
    }
}

@Composable
private fun EmptyProductsState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.empty_shelf_img),
            title = stringResource(Res.string.empty_shelf_title),
            description = "",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}