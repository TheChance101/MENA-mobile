package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.best_selling
import mena.dukan_presentation.generated.resources.favorite_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_share
import mena.dukan_presentation.generated.resources.ic_shopping_bag_add
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.product_image
import mena.dukan_presentation.generated.resources.share_icon
import mena.dukan_presentation.generated.resources.shopping_basket_icon
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.LoadingProductsHeader
import net.thechance.mena.dukan.presentation.component.ProductsHeader
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.pagination.PagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.DukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelvesState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelves: Pager<Int, ShelfUiState>,
) {
    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            AppBar(
                state = state.dukanInfo,
                listener = listener
            )
        }
    ) {
        DukanContent(
            state = state,
            listener = listener,
            pagerShelves = pagerShelves
        )
    }
}

@Composable
private fun AppBar(
    state: DukanInfo,
    listener: DukanDetailsInteractionListener
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._8)
    ) {
        AppBarOptionContainer(
            onClick = listener::onBackClicked
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                tint = Theme.colorScheme.primary.primary,
                contentDescription = stringResource(Res.string.back_arrow),
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = state.name,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.medium,
            modifier = Modifier.padding(start = Theme.spacing._8, end = Theme.spacing._4)
                .weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)) {
            AppBarOptionContainer(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_share),
                    tint = Theme.colorScheme.primary.primary,
                    contentDescription = stringResource(Res.string.share_icon),
                    modifier = Modifier.size(40.dp)
                )
            }
            AppBarOptionContainer(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_favorite),
                    tint = Theme.colorScheme.primary.primary,
                    contentDescription = stringResource(Res.string.favorite_icon),
                    modifier = Modifier.size(40.dp)
                )
            }
            AppBarOptionContainer(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_shopping_basket),
                    tint = Theme.colorScheme.primary.primary,
                    contentDescription = stringResource(Res.string.shopping_basket_icon),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}


@Composable
private fun DukanContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelves: Pager<Int, ShelfUiState>
) {
    val lazyRowListState = rememberLazyListState()
    lazyRowListState.LoadMoreOnScroll(pagerShelves)
    val lazyColumnListState = rememberLazyListState()
    lazyColumnListState.LoadMoreOnScroll(pagerShelves)
    val coroutineScope = rememberCoroutineScope()
    var chipsAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = lazyColumnListState) {
        snapshotFlow {
            lazyColumnListState.layoutInfo
        }.collect { layoutInfo ->
            val isBestSellingVisible = layoutInfo.visibleItemsInfo
                .any { it.key == "BestSelling" }
            chipsAlpha = if (isBestSellingVisible) 0f else 1f
        }
    }
    LaunchedEffect(key1 = lazyColumnListState) {
        snapshotFlow { lazyColumnListState.firstVisibleItemIndex }.collect { index ->
            val shelfIndex = index - 2
            if (shelfIndex >= 0 && shelfIndex < state.shelves.items.size) {
                val shelfId = state.shelves.items[shelfIndex].id
                if (shelfId != state.shelfIdSelected) {
                    listener.onShelfClicked(shelfId)
                    coroutineScope.launch {
                        lazyRowListState.animateScrollToItem(shelfIndex)
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = Theme.spacing._8),
        state = lazyColumnListState
    ) {
        item(key = "BestSelling") {
            BestSellingState(
                state = state,
                listener = listener
            )
        }

        stickyHeader(key = "ShelvesChips") {
            DukanShelvesChips(
                state = state,
                listener = listener,
                lazyRowState = lazyRowListState,
                lazyColumnState = lazyColumnListState,
                coroutineScope = coroutineScope,
                alpha = chipsAlpha
            )
        }
        shelvesWithProducts(
            state = state,
            listener = listener
        )
    }
}

@Composable
private fun DukanShelvesChips(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    lazyRowState: LazyListState,
    lazyColumnState: LazyListState,
    coroutineScope: CoroutineScope,
    alpha: Float
) {
    if (alpha == 0f) {
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface)
                .padding(vertical = Theme.spacing._8),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(horizontal = Theme.spacing._16),
            state = lazyRowState
        ) {
            items(count = state.shelves.items.size, key = { state.shelves.items[it].id }) {
                val shelf = state.shelves.items[it]
                Chip(
                    text = shelf.name,
                    isSelected = (shelf.id == state.shelfIdSelected),
                    onClick = {
                        listener.onShelfClicked(shelf.id)
                        coroutineScope.launch {
                            lazyColumnState.animateScrollToItem(it + 2)
                        }
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Theme.spacing._4)
                .blur(Theme.spacing._8)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x14000000),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

private fun LazyListScope.shelvesWithProducts(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
) {
    when (state.shelvesState) {
        ShelvesState.LOADING -> {
            items(4) {
                ShelfWithProductsItemShimmer()
            }
        }

        ShelvesState.LOADED -> {
            items(count = state.shelves.items.size, key = { state.shelves.items[it].id }) {
                val shelf = state.shelves.items[it]
                Column(
                    Modifier.padding(horizontal = Theme.spacing._16)
                        .padding(top = Theme.spacing._16)
                ) {
                    ProductsHeader(
                        shelfName = shelf.name,
                        onClick = {
                            listener.onViewAllShelfProductsClicked(
                                shelf.id,
                                shelf.name
                            )
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._8)
                    )
                    shelf.products.forEachIndexed { index, product ->
                        val topPadding = if (index > 0) Theme.spacing._8 else 0.dp
                        ProductCard(
                            productName = product.name,
                            productImageUrl = product.imageUrl,
                            productDescription = product.description,
                            productPrice = product.price,
                            productAction = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_shopping_bag_add),
                                    tint = Color(state.dukanInfo.color),
                                    contentDescription = stringResource(Res.string.add_product),
                                    modifier = Modifier.size(36.dp)
                                        .clip(RoundedCornerShape(Theme.radius.md))
                                        .border(
                                            1.dp,
                                            Theme.colorScheme.stroke,
                                            RoundedCornerShape(Theme.radius.md)
                                        )
                                        .clickable(onClick = { })
                                        .padding(Theme.spacing._8)
                                )
                            },
                            modifier = Modifier.padding(top = topPadding)
                        )
                    }
                }
            }
        }

        ShelvesState.EMPTY -> {
        }
    }
}

@Composable
private fun ShelfWithProductsItemShimmer() {
    Column(
        Modifier.fillMaxWidth()
            .padding(top = Theme.spacing._16)
            .padding(horizontal = Theme.spacing._16)
    ) {
        LoadingProductsHeader()
        Spacer(Modifier.height(Theme.spacing._8))
        LoadingProductCard(backgroundColor = Theme.colorScheme.background.surface)
        Spacer(Modifier.height(Theme.spacing._16))
        LoadingProductCard(backgroundColor = Theme.colorScheme.background.surface)
    }
}

@Composable
private fun BestSellingShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        LoadingProductsHeader(Modifier.padding(horizontal = Theme.spacing._16))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(
                horizontal = Theme.spacing._8,
                vertical = Theme.spacing._12
            )
        ) {
            items(8) {
                BestSellingProductItemShimmer()
            }
        }
    }
}

@Composable
fun BestSellingProductItemShimmer() {
    Column(
        modifier = Modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(
                    color = Theme.colorScheme.background.surfaceHigh,
                    shape = RoundedCornerShape(Theme.radius.full)
                )
        )

        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._4)
                .padding(horizontal = Theme.spacing._12)
                .height(Theme.spacing._16)
                .background(
                    color = Theme.colorScheme.background.surfaceHigh,
                    shape = RoundedCornerShape(Theme.radius.md)
                )
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._12)
                .padding(top = Theme.spacing._2)
                .height(Theme.spacing._16)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .padding(start = Theme.spacing._4)
                    .width(68.dp)
                    .height(Theme.spacing._24)
                    .background(
                        color = Theme.colorScheme.background.surfaceHigh,
                        shape = RoundedCornerShape(Theme.radius.md)
                    )
            )
            Image(
                painter = painterResource(Res.drawable.silver_tc),
                contentDescription = stringResource(Res.string.koin_icon),
                modifier = Modifier
                    .padding(start = Theme.spacing._4)
                    .size(Theme.spacing._16)
            )
        }
    }
}

@Composable
private fun BestSellingState(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener
) {
// Handle best selling state
}

@Composable
private fun BestSelling(
    products: List<ProductUiState>,
    listener: DukanDetailsInteractionListener,
    iconColor: Long,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        ProductsHeader(
            shelfName = stringResource(Res.string.best_selling),
            onClick = {},
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surfaceHigh),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(
                horizontal = Theme.spacing._8,
                vertical = Theme.spacing._12
            )
        ) {
            items(dummyProducts.size) {
                BestSellingProductItem(
                    product = dummyProducts[it],
                    iconColor = iconColor
                )
            }
        }
    }
}

@Composable
private fun BestSellingProductItem(product: ProductUiState, iconColor: Long) {
    Column(
        modifier = Modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(100.dp)) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = stringResource(Res.string.product_image),
                modifier = Modifier.fillMaxSize()
                    .clip(RoundedCornerShape(Theme.radius.full)),
                contentScale = ContentScale.Crop
            )
            Icon(
                painter = painterResource(Res.drawable.ic_shopping_bag_add),
                tint = Color(iconColor),
                contentDescription = stringResource(Res.string.add_product),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
                    .clip(RoundedCornerShape(Theme.radius.full))
                    .background(Theme.colorScheme.background.surfaceLow).padding(9.dp)
            )
        }
        Text(
            text = product.name,
            style = Theme.typography.label.small,
            modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._4),
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.price.toString(),
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary,
            )
            Image(
                painter = painterResource(Res.drawable.silver_tc),
                contentDescription = stringResource(Res.string.koin_icon),
                modifier = Modifier
                    .padding(start = Theme.spacing._4)
                    .size(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NoImageDukanDetailsPreview() {
    MenaTheme {
        NoImageDukanDetails(
            state = DukanDetailsUiState(
                DukanInfo(
                    name = "Calvin Klein store international",
                    color = 0xFFFB5B5D
                ),
                shelvesState = ShelvesState.LOADED,
                shelfIdSelected = "1",
                shelves = PagingData(dummyShelves)
            ),
            listener = PreviewDukanDetailsInteractionListener,
            pagerShelves = Pager(
                config = net.thechance.mena.dukan.presentation.util.pagination.PagingConfig(),
                pagingSourceFactory = {
                    object : PagingSource<Int, ShelfUiState>() {
                        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShelfUiState> {
                            return LoadResult.Page(dummyShelves, null, null)
                        }
                    }
                }
            )
        )
    }
}


val dummyProducts = listOf(
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    )

)
val dummyShelves = listOf(
    ShelfUiState(
        id = "1",
        name = "Clothes",
        products = dummyProducts
    ),
    ShelfUiState(
        id = "2",
        name = "Perfumes",
        products = dummyProducts
    ),
    ShelfUiState(
        id = "3",
        name = "Accessories",
        products = dummyProducts
    ),
    ShelfUiState(
        id = "4",
        name = "Shoes",
        products = dummyProducts
    ),
    ShelfUiState(
        id = "5",
        name = "Watch",
        products = dummyProducts
    ),
    ShelfUiState(
        id = "6",
        name = "Top",
        products = dummyProducts
    )
)