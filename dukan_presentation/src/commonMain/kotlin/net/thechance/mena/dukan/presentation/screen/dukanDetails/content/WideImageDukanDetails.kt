package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_share
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.products
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ShelfChip
import net.thechance.mena.dukan.presentation.component.productCard.PriceWithIcon
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WideImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelf: Pager<Int, DukanDetailsUiState.ShelfUiState>,
    pagerProduct: Pager<Int, DukanDetailsUiState.ProductUiState>
) {
    OnSystemBackPressed { listener::onBackClicked }
    Scaffold(
        topBar = {
            AppBar(
                title = "",
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow)
                    )
                },
                onLeadingClick = { listener::onBackClicked },
                trailingContent = {
                    DukanHeaderIcon(
                        icon = painterResource(Res.drawable.ic_shopping_basket),
                        onIconClick = {}
                    )
                }
            )
        }, modifier = Modifier.fillMaxSize()
    ) {
        Box {
            LazyColumn {
                item { DukanHeader(state = state.dukanInfo) }
                item { DukanShelvesSection(state = state, listener = listener) }
                item { DukanProductsSection(state = state) }
            }
            DukanActionButtons(state = state.dukanInfo, modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun DukanHeader(state: DukanDetailsUiState.DukanInfo) {
    DukanImageAndTitle(
        state = state,
        modifier = Modifier.padding(horizontal = Theme.spacing._16)
    )
}

@Composable
private fun DukanActionButtons(
    state: DukanDetailsUiState.DukanInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = Theme.spacing._16 + Theme.spacing._4)) {
        DukanIconButton(
            icon = painterResource(Res.drawable.ic_favorite),
            iconColor = Color(state.color),
        )
        DukanIconButton(
            icon = painterResource(Res.drawable.ic_share),
            iconColor = Color(state.color),
        )
    }
}

@Composable
private fun DukanHeaderIcon(
    icon: Painter,
    isBadgeVisible: Boolean = false,
    onIconClick: () -> Unit
) {
    AppBarOptionContainer(onClick = { onIconClick() }, isBadgeVisible = isBadgeVisible) {
        Icon(painter = icon, contentDescription = null)
    }
}

@Composable
private fun DukanImageAndTitle(
    state: DukanDetailsUiState.DukanInfo,
    modifier: Modifier = Modifier
) {
    val shadowColor = Color(state.color).copy(alpha = 0.3f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(Theme.radius.md),
                spotColor = shadowColor,
                ambientColor = shadowColor
            )
            .clip(RoundedCornerShape(Theme.radius.md)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = state.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = state.name,
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.primary.onPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = Theme.spacing._8, bottom = Theme.spacing._8)
        )
    }
}

@Composable
private fun DukanIconButton(
    icon: Painter,
    iconColor: Color,
    onIconClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.full))
            .clickable(
                onClick = onIconClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.full)
            )
            .border(
                width = 3.dp,
                color = Theme.colorScheme.background.surface,
                shape = RoundedCornerShape(Theme.radius.full)
            )
    ) {
        Icon(painter = icon, contentDescription = null, tint = iconColor)
    }
}


@Composable
private fun DukanShelvesSection(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener
) {
    Text(
        text = stringResource(Res.string.products),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(horizontal = Theme.spacing._16)
            .padding(top = Theme.spacing._16)
    )
    AnimatedContent(
        targetState = state.shelvesState,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
        label = "Shelves Animation",
        modifier = Modifier.padding(vertical = Theme.spacing._16)
    ) { targetState ->
        when (targetState) {
            DukanDetailsUiState.ShelvesState.LOADING -> LoadingShelves()
            DukanDetailsUiState.ShelvesState.LOADED -> LoadedShelves(
                shelves = state.shelves.items,
                selectedShelfId = state.shelfIdSelected,
                onShelfClick = listener::onShelfClicked,
                chipColor = Color(state.dukanInfo.color)
            )

            DukanDetailsUiState.ShelvesState.EMPTY -> {}
        }
    }
}

@Composable
private fun LoadingShelves() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(count = 8) {
            Chip(
                text = "             ",
                isSelected = false,
                isEnabled = false,
                onClick = {})
        }
    }
}

@Composable
private fun LoadedShelves(
    shelves: List<DukanDetailsUiState.ShelfUiState>,
    selectedShelfId: String?,
    onShelfClick: (shelfId: String) -> Unit,
    chipColor: Color
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(items = shelves, key = { it.id }) { shelf ->
            ShelfChip(
                text = shelf.name,
                isSelected = (shelf.id == selectedShelfId),
                onClick = { onShelfClick(shelf.id) },
                selectedBackgroundColor = chipColor,
            )
        }
    }
}

@Composable
private fun DukanProductsSection(state: DukanDetailsUiState, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = state.productsState,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
        label = "Products Animation",
        modifier = modifier
    ) { targetState ->
        when (targetState) {
            DukanDetailsUiState.ProductsState.LOADING -> LoadingProductsGrid()
            DukanDetailsUiState.ProductsState.LOADED -> ProductsGrid(products = state.productsShelf.items)
            DukanDetailsUiState.ProductsState.EMPTY -> {}
        }
    }
}

@Composable
private fun LoadingProductsGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        userScrollEnabled = false,
        modifier = Modifier.height(500.dp)
    ) {
        items(count = 6) {
            ProductCard(
                imageUrl = "",
                title = "          ",
                price = "      ",
                onClick = {},
                isEnabled = false
            )
        }
    }
}

@Composable
private fun ProductsGrid(
    products: List<DukanDetailsUiState.ProductUiState>,
    modifier: Modifier = Modifier
) {
    val rows = (products.size + 1) / 2
    val gridHeight = (rows * 240.dp) + ((rows - 1) * Theme.spacing._8)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.height(gridHeight),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        userScrollEnabled = false
    ) {
        items(items = products, key = { it.id }) { product ->
            ProductCard(
                imageUrl = product.imageUrl,
                title = product.name,
                price = "$${product.price}",
                onClick = {}
            )
        }
    }
}

@Composable
private fun ProductCard(
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
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(Theme.radius.sm))
        )
        Text(
            text = title,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = Theme.spacing._8)
                .padding(top = Theme.spacing._16)
                .align(Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Theme.spacing._4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.discount_icon),
                contentDescription = null,
                modifier = modifier.padding(end = Theme.spacing._4)
            )
            PriceWithIcon(
                price = price,
                iconRes = Res.drawable.silver_tc,
                contentDescription = stringResource(Res.string.koin_icon),
            )
        }
    }
}

@Preview(showBackground = true, name = "Image and Title")
@Composable
private fun DukanImageAndTitlePreview() {
    MenaTheme {
        DukanImageAndTitle(
            state = DukanDetailsUiState.DukanInfo(
                name = "My Awesome Dukan",
                imageUrl = "https://via.placeholder.com/400x188",
                color = 0xFF4CAF50
            )
        )
    }
}

@Preview(showBackground = true, name = "Icon Buttons")
@Composable
private fun DukanIconButtonPreview() {
    MenaTheme {
        Column {
            DukanIconButton(
                icon = painterResource(Res.drawable.ic_favorite),
                iconColor = Theme.colorScheme.secondary.secondary,
                onIconClick = {}
            )
            DukanIconButton(
                icon = painterResource(Res.drawable.ic_share),
                iconColor = Theme.colorScheme.secondary.secondary,
                onIconClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Shelves - Loading State")
@Composable
private fun DukanShelvesRowLoadingPreview() {
    MenaTheme {
        LoadingShelves()
    }
}

@Preview(showBackground = true, name = "Shelves - Loaded State")
@Composable
private fun DukanShelvesRowLoadedPreview() {
    MenaTheme {
        LoadedShelves(
            shelves = listOf(
                DukanDetailsUiState.ShelfUiState(id = "1", name = "Dairy & Eggs"),
                DukanDetailsUiState.ShelfUiState(id = "2", name = "Fresh Produce"),
                DukanDetailsUiState.ShelfUiState(id = "3", name = "Bakery"),
            ),
            selectedShelfId = "2",
            onShelfClick = {},
            chipColor = Theme.colorScheme.secondary.secondary
        )
    }
}

@Preview(showBackground = true, name = "Products Section - Loading")
@Composable
private fun DukanProductsSectionLoadingPreview() {
    MenaTheme {
        DukanProductsSection(
            state = DukanDetailsUiState(productsState = DukanDetailsUiState.ProductsState.LOADING)
        )
    }
}

@Preview(showBackground = true, name = "Products Section - Loaded")
@Composable
private fun DukanProductsSectionLoadedPreview() {
    val mockProducts = fakeProducts().map {
        DukanDetailsUiState.ProductUiState(
            id = it.id, name = it.name, imageUrl = it.imageUrl, price = it.price,
            description = it.description ?: ""
        )
    }
    MenaTheme {
        DukanProductsSection(
            state = DukanDetailsUiState(
                productsState = DukanDetailsUiState.ProductsState.LOADED,
                productsShelf = PagingData(items = mockProducts)
            )
        )
    }
}
@Preview(showBackground = true, name = "Header Icon")
@Composable
private fun DukanHeaderIconPreview() {
    MenaTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            DukanHeaderIcon(
                icon = painterResource(Res.drawable.ic_shopping_basket),
                isBadgeVisible = true,
                onIconClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Action Buttons")
@Composable
private fun DukanActionButtonsPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DukanActionButtons(
                state = DukanDetailsUiState.DukanInfo(color = 0xFF4CAF50),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Preview(showBackground = true, name = "Products Grid Standalone")
@Composable
private fun ProductsGridPreview() {
    val mockProducts = fakeProducts().map {
        DukanDetailsUiState.ProductUiState(
            id = it.id, name = it.name, imageUrl = it.imageUrl, price = it.price,
            description = it.description ?: ""
        )
    }
    MenaTheme {
        ProductsGrid(products = mockProducts)
    }
}