package net.thechance.mena.dukan.presentation.screen.dukanCart.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.arrow_right_icon
import mena.dukan_presentation.generated.resources.back_to_main_screen_icon
import mena.dukan_presentation.generated.resources.cart
import mena.dukan_presentation.generated.resources.delete_icon
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.from_dukan
import mena.dukan_presentation.generated.resources.go_to_checkout
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_arrow_right
import mena.dukan_presentation.generated.resources.ic_delete
import mena.dukan_presentation.generated.resources.sub_total_price
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.product.PriceWithIcon
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.product.ProductQuantityButton
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanCartInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.dukanCartUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Composable
fun DukanCartContent(state: DukanCartUiState, listener: DukanCartInteractionListener) {
    val products = state.products.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = { TopBar(listener::onBackClicked) },
        bottomBar = {
            BottomBar(
                totalPrice = state.totalPrice,
                onCheckoutClick = listener::onCheckoutClicked
            )
        },
        snakeBar = {
            state.snackBarState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onDismissSnackBar
                )
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = Theme.spacing._8),
            modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.spacing._16),
            state = lazyListState,
        ) {
            item(key = "dukan_info") {
                AnimatedContent(state.dukanInfoState) {
                    when (it) {
                        DukanCartUiState.DukanInfoState.LOADING -> DukanInfoSkeleton()
                        DukanCartUiState.DukanInfoState.LOADED -> {
                            DukanInfo(
                                dukanName = state.dukanInfo.name,
                                dukanImageUrl = state.dukanInfo.imageUrl,
                                onClick = listener::onDukanClicked
                            )
                        }
                    }
                }
            }

            if (products.loadState.refresh is androidx.paging.LoadState.Loading) {
                items(count = 8) {
                    LoadingProductCard()
                }
            } else {
                items(count = products.itemCount, key = { products[it]?.id.orEmpty() }) { index ->
                    val product = products[index] ?: return@items
                    SwipeableItem(
                        actionButton = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_delete),
                                contentDescription = stringResource(Res.string.delete_icon),
                                tint = Theme.colorScheme.error,
                                modifier = Modifier
                                    .padding(vertical = Theme.spacing._8)
                                    .width(48.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topEnd = Theme.radius.md,
                                            bottomEnd = Theme.radius.md
                                        )
                                    )
                                    .background(Theme.colorScheme.background.bgError)
                                    .clickable(onClick = { listener.onRemoveItemClicked(product.id) })
                                    .padding(
                                        vertical = Theme.spacing._32,
                                        horizontal = Theme.spacing._12
                                    )
                            )
                        }
                    ) {
                        ProductCard(
                            productName = product.name,
                            productDescription = product.description,
                            productImageUrl = product.imageUrl,
                            productPrice = product.price,
                            productCardBackground = Theme.colorScheme.background.surfaceLow,
                            modifier = Modifier.fillMaxWidth(),
                            productAction = {
                                ProductQuantityButton(
                                    onPlusClick = {
                                        listener.onIncreaseItemQuantityClicked(
                                            product.id,
                                            product.quantity + 1
                                        )
                                    },
                                    onMinusClick = {
                                        listener.onDecreaseItemQuantityClicked(
                                            product.id,
                                            product.quantity - 1
                                        )
                                    },
                                    inCartQuantity = product.quantity
                                )
                            }
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    AppBar(
        title = stringResource(resource = Res.string.cart),
        titleColor = Theme.colorScheme.shadePrimary,
        leadingContent = {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_arrow_left),
                contentDescription = stringResource(resource = Res.string.back_to_main_screen_icon),
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = onBackClick,
    )
}

@Composable
fun DukanInfo(
    dukanName: String,
    dukanImageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colorScheme.background.surface)
            .padding(bottom = Theme.spacing._4)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clickable(onClick = { onClick() }, indication = null, interactionSource = null)
            .padding(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        AsyncImage(
            model = dukanImageUrl,
            contentDescription = stringResource(Res.string.dukan_image),
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Theme.radius.full)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)
        ) {
            Text(
                text = stringResource(Res.string.from_dukan),
                style = Theme.typography.label.extraSmall,
                color = Theme.colorScheme.shadeSecondary,
            )
            Text(
                text = dukanName,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary,
            )
        }
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = stringResource(Res.string.arrow_right_icon),
            tint = Theme.colorScheme.primary.primary,
            modifier = Modifier.size(24.dp)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._2 + Theme.spacing._4)
        )
    }
}

@Composable
private fun DukanInfoSkeleton() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Theme.colorScheme.background.surface)
            .padding(bottom = Theme.spacing._4)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceHigh,
                shape = RoundedCornerShape(Theme.radius.md)
            )
    )
}

@Composable
private fun BottomBar(totalPrice: Double, onCheckoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = Theme.radius.xl, topEnd = Theme.radius.xl))
            .background(Theme.colorScheme.background.surfaceLow)
            .shadow(
                elevation = 118.dp,
                spotColor = Color.Black.copy(.04f),
                ambientColor = Color.Black.copy(.04f),
                shape = RoundedCornerShape(topStart = Theme.radius.xl, topEnd = Theme.radius.xl)
            )
            .clickable(onClick = { onCheckoutClick() }, indication = null, interactionSource = null)
            .padding(Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(resource = Res.string.sub_total_price),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeSecondary,
                modifier = Modifier.weight(1f)
            )
            PriceWithIcon(
                price = totalPrice.toString().replace('.', ','),
            )
        }
        PrimaryButton(
            text = stringResource(resource = Res.string.go_to_checkout),
            onClick = onCheckoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(
                    elevation = 12.dp,
                    spotColor = Color.White.copy(.16f),
                    ambientColor = Color.White.copy(.16f),
                    shape = RoundedCornerShape(Theme.radius.md)
                ),
            isEnabled = totalPrice > 0.0
        )
    }
}

@Composable
private fun SwipeableItem(
    actionButton: @Composable (BoxScope.() -> Unit),
    modifier: Modifier = Modifier,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var actionButtonWidth by remember { mutableFloatStateOf(0f) }
    val offset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier
                .align(if (isRtl) Alignment.CenterStart else Alignment.CenterEnd)
                .onSizeChanged {
                    actionButtonWidth = it.width.toFloat()
                },
            contentAlignment = Alignment.Center,
            content = actionButton
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(-offset.value.roundToInt(), 0) }
                .pointerInput(true) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                val adjustedDragAmount = if (isRtl) -dragAmount else dragAmount
                                val newOffset =
                                    (offset.value - adjustedDragAmount).coerceIn(
                                        0f,
                                        actionButtonWidth
                                    )
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            when {
                                offset.value >= actionButtonWidth / 2f -> {
                                    coroutineScope.launch {
                                        offset.animateTo(actionButtonWidth)
                                        onExpanded()
                                    }
                                }

                                else -> {
                                    coroutineScope.launch {
                                        offset.animateTo(0f)
                                        onCollapsed()
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}


@Preview
@Composable
private fun DukanCartContentPreview() {
    MenaTheme {
        DukanCartContent(state = dukanCartUiState, listener = PreviewDukanCartInteractionListener)
    }
}