package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
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
            modifier = Modifier.padding(start = Theme.spacing._8).weight(1f),
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
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .shadow(
                elevation = Theme.spacing._4,
                shape = RectangleShape,
                spotColor = Color(0x14000000)
            )
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
    AnimatedContent(
        targetState = state.shelvesState,  // should be bestSellingState
        transitionSpec = { fadeTransitionSpec() },
        label = "ShelvesContentAnimation"
    ) { target ->
        when (target) {
            ShelvesState.LOADING -> {
                BestSellingShimmer()
            }

            ShelvesState.LOADED -> {
                BestSelling(
                    products = state.bestSellingProducts.items,
                    iconColor = state.dukanInfo.color,
                    listener = listener
                )
            }

            ShelvesState.EMPTY -> {
            }
        }
    }
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
        imageUrl = "data:image/jpeg;base64,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"
    ),
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "data:image/jpeg;base64,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"
    ),
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "data:image/jpeg;base64,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"
    ),
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "data:image/jpeg;base64,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"
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