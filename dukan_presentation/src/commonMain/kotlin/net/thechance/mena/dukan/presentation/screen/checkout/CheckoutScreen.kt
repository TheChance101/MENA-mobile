package net.thechance.mena.dukan.presentation.screen.checkout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.checkout
import mena.dukan_presentation.generated.resources.confirm_order
import mena.dukan_presentation.generated.resources.deliver_to
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_home
import mena.dukan_presentation.generated.resources.ic_maps_editing
import mena.dukan_presentation.generated.resources.silver_tc
import mena.dukan_presentation.generated.resources.summary_details
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.checkout.CartItem
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutEffect
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutUiState
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun CheckoutScreen(
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            CheckoutEffect.NavigateBack -> {
                navController.popBackStack()
            }

            CheckoutEffect.NavigateToChangeLocation -> {
                // TODO
            }
        }
    }

    CheckoutContent(
        state = state,
        listener = viewModel
    )

}

@Composable
fun CheckoutContent(state: CheckoutUiState, listener: CheckoutViewModel) {
    val products = state.items.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            CheckoutAppBar()
        },
        bottomBar = {
            ConfirmOrderButton()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)

        ) {
            DeliveryAddressCard(modifier = Modifier.padding(top = 8.dp))
            CheckoutSummaryCard(products = products, modifier = Modifier.padding(top = 16.dp))
        }

    }
}

@Composable
private fun CheckoutSummaryCard(
    products: LazyPagingItems<CartItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(Res.string.summary_details),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 8.dp
                    )
                )
                .background(Theme.colorScheme.background.surfaceLow)
        ) {
            Box(modifier = Modifier.wrapContentHeight()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .padding(horizontal = 12.dp),
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = products.itemCount,
                            key = products.itemKey { it.quantity }
                        ) { index ->
                            val cartItem = products[index]
                            if (cartItem != null) {
                                CheckoutProductItem(cartItem = cartItem)
                            }
                        }
                    }
                    CheckoutFeeItem(
                        modifier = Modifier.padding(top = 13.dp, bottom = 24.dp),
                        productName = "Platform fees",
                        price = 1.99
                    )
                    DashedSeparator(
                        modifier = Modifier
                            .padding(bottom = 11.dp)
                            .padding(horizontal = 2.dp)
                    )
                }
                HalfCircle(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(end = 2.dp)
                        .offset(x = -11.dp)
                )
                HalfCircle(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(start = 2.dp)
                        .offset(x = 11.dp)
                )

            }
            Box(modifier = Modifier.wrapContentHeight()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp, bottom = 33.dp)
                        .padding(horizontal = 12.dp),
                ) {
                    CheckoutFeeItem(productName = "Total amount", price = 31.99)

                }
                HalfCircleList(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 4.dp)
                        .offset(y = 10.5.dp)
                )
            }
        }
    }
}

@Composable
private fun CheckoutAppBar() {
    AppBar(
        title = stringResource(Res.string.checkout),
        onLeadingClick = {},
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._8
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow),
            )
        }
    )
}

@Composable
private fun DeliveryAddressCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._8),
            text = stringResource(Res.string.deliver_to),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(Theme.spacing._12))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(Theme.spacing._12))
                    .background(Theme.colorScheme.background.surface)
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(Res.drawable.ic_home),
                    contentDescription = ""
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Home",
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.shadePrimary
                )
                Text(
                    text = "Karrada, Baghdad 123 St.",
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }
            Image(
                modifier = Modifier.padding(end = 6.dp),
                painter = painterResource(Res.drawable.ic_maps_editing),
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun CheckoutProductItem(
    cartItem: CartItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surface)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = cartItem.quantity.toString(),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
                .weight(1f),
            text = cartItem.name,
            maxLines = 1,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Text(
            text = cartItem.price.toString(),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Icon(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(20.dp),
            painter = painterResource(Res.drawable.silver_tc),
            contentDescription = ""

        )
    }
}

@Composable
private fun CheckoutFeeItem(
    productName: String,
    price: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
                .weight(1f),
            text = productName,
            maxLines = 1,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Text(
            text = price.toString(),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Icon(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(20.dp),
            painter = painterResource(Res.drawable.silver_tc),
            contentDescription = ""

        )
    }
}

@Composable
fun DashedSeparator(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color(0XFFEAECF0),
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
                0f
            )
        )
    }
}

@Composable
private fun HalfCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(21.dp)
            .clip(CircleShape)
            .background(Theme.colorScheme.background.surface)
    )
}

@Composable
fun HalfCircleList(modifier: Modifier = Modifier) {
    var widthDp by remember { mutableStateOf(0.dp) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()

    ) {
        widthDp = maxWidth
        val circleSize = 21.dp
        val spacing = 2.dp
        val count = remember(widthDp) {
            if (widthDp > 0.dp)
                (widthDp / (circleSize + spacing)).toInt()
            else 0
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(count) {
                HalfCircle()
            }
        }
    }
}

@Composable
private fun ConfirmOrderButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = Theme.spacing._24,
                    topEnd = Theme.spacing._24
                )
            )
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(Theme.spacing._16),
    ) {
        PrimaryButton(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            text = stringResource(Res.string.confirm_order),
            onClick = {
                // TODO add confirm action
            },
            contentPadding = PaddingValues(vertical = Theme.spacing._12)
        )
    }
}

@Preview
@Composable
private fun CheckoutScreenPreview() {
    MenaTheme {
        CheckoutScreen()
    }
}