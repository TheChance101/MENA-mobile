package net.thechance.mena.dukan.presentation.screen.dukanCart.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.arrow_right_icon
import mena.dukan_presentation.generated.resources.back_to_main_screen_icon
import mena.dukan_presentation.generated.resources.cart
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.from_dukan
import mena.dukan_presentation.generated.resources.go_to_checkout
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_arrow_right
import mena.dukan_presentation.generated.resources.sub_total_price
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.PriceWithIcon
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.product.SetProductQuantity
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanCartInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.dukanCartUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanCartContent(state: DukanCartUiState, listener: DukanCartInteractionListener) {
    val products = state.products.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            TopBar(
                onBackClick = listener::onBackClick
            )
        },
        bottomBar = {
            BottomBar(totalPrice = state.totalPrice, onCheckoutClick = listener::onCheckoutClick)
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = Theme.spacing._12),
            modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.spacing._16),
            state = lazyListState,
        ) {
            stickyHeader(key = "dukan_info") {
                DukanInfo(
                    dukanName = state.dukanDetails.name,
                    dukanImageUrl = state.dukanDetails.imageUrl,
                    onClick = listener::onDukanDetailsClick
                )
            }
            items(count = products.itemCount, key = { products[it]?.id.orEmpty() }) { index ->
                val product = products[index] ?: return@items
                ProductCard(
                    productName = product.name,
                    productDescription = product.description,
                    productImageUrl = product.imageUrl,
                    productPrice = product.price,
                    productCardBackground = Theme.colorScheme.background.surfaceLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SetProductQuantity(
                        onAddProductClick = { listener.onIncreaseItemQuantityClick(product.id) },
                        onRemoveProductClick = { listener.onDecreaseItemQuantityClick(product.id) }
                    )
                }

            }
        }
    }
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
            .padding(bottom = Theme.spacing._4)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clickable(onClick = { onClick })
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
        )
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
private fun BottomBar(totalPrice: Double, onCheckoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = Theme.radius.xl, topEnd = Theme.radius.xl))
            .background(Theme.colorScheme.background.surfaceLow)
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
            modifier = Modifier.fillMaxWidth().height(48.dp),
            isEnabled = totalPrice > 0.0
        )
    }
}


@Preview
@Composable
private fun DukanCartContentPreview() {
    MenaTheme {
        DukanCartContent(state = dukanCartUiState, listener = PreviewDukanCartInteractionListener)
    }
}