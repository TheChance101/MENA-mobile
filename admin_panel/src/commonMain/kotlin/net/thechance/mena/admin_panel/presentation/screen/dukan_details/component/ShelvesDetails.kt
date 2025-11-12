package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.presentation.utils.PaginationTrigger
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.img_silver
import net.thechance.mena.admin_panel.resources.product_img
import net.thechance.mena.admin_panel.resources.shelves
import net.thechance.mena.admin_panel.resources.silver_img
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ShelvesDetails(
    totalShelves: String,
    shelves: List<Shelf>,
    selectedShelf: String,
    onShelfClicked: (String) -> Unit,
    onNextShelvesPageRequested: () -> Unit,
    onNextProductsPageRequested: () -> Unit,
    products: List<Product>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
    ) {
        ShelfHeader(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            totalShelves = totalShelves
        )

        shelves(
            modifier = Modifier.padding(top = 12.dp),
            shelves = shelves,
            selectedShelf = selectedShelf,
            onShelfClicked = onShelfClicked,
            onNextPageRequested = onNextShelvesPageRequested,
        )

        products(
            modifier = Modifier.padding(top = 12.dp),
            products = products,
            onNextPageRequested = onNextProductsPageRequested,
        )
    }
}

@Composable
private fun ShelfHeader(
    totalShelves: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.shelves),
            style = Theme.typography.title.large,
            color = Theme.colorScheme.shadePrimary
        )
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .widthIn(min = 32.dp)
                .heightIn(min = 32.dp)
                .background(
                    color = Theme.colorScheme.background.surface,
                    shape = CircleShape
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = totalShelves,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary
            )
        }
    }
}

@Composable
private fun shelves(
    shelves: List<Shelf>,
    selectedShelf: String,
    onShelfClicked: (String) -> Unit,
    onNextPageRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    PaginationTrigger(
        list = shelves,
        listState = listState,
        buffer = 5,
        loadNextItems = onNextPageRequested
    )

    LazyRow(
        modifier = modifier,
        state = listState,
    ) {
        items(shelves) { shelf ->
            Chip(
                modifier = Modifier.padding(start = 8.dp),
                text = shelf.title,
                isSelected = shelf.id == selectedShelf,
                onClick = { onShelfClicked(shelf.id) }
            )
        }
    }
}

@Composable
private fun products(
    products: List<Product>,
    onNextPageRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    PaginationTrigger(
        list = products,
        listState = listState,
        buffer = 5,
        loadNextItems = onNextPageRequested
    )

    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(products) { product ->
            ProductCard(
                modifier = Modifier.padding(bottom = 8.dp),
                product = product
            )
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
        AsyncImage(
            modifier = Modifier
                .padding(end = 8.dp, start = 16.dp)
                .size(96.dp)
                .clip(RoundedCornerShape(Theme.radius.sm)),
            model = product.imageUrls.any(),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(Res.string.product_img),
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
            Row {
                product.discountedPrice?.let {
                    Text(
                        text = it.toString(),
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.shadeTertiary
                    )
                }

                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = product.price.toString(),
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
    }
}