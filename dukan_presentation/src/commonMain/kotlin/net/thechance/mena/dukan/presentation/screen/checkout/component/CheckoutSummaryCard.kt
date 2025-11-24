package net.thechance.mena.dukan.presentation.screen.checkout.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount
import mena.dukan_presentation.generated.resources.platform_fees
import mena.dukan_presentation.generated.resources.silver_tc
import mena.dukan_presentation.generated.resources.silver_tier_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
fun CheckoutSummaryCard(
    products: LazyPagingItems<CheckoutUiState.CartItem>,
    cartDetails: CheckoutUiState.CartDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(
                SquircleShape(
                    topStart = Theme.spacing._16,
                    topEnd = Theme.spacing._16,
                    bottomStart = Theme.spacing._8,
                    bottomEnd = Theme.spacing._8
                )
            )
            .background(Theme.colorScheme.background.surfaceLow)
    ) {
        Box(modifier = Modifier.wrapContentHeight()) {
            SummaryItemsList(cartDetails = cartDetails, products = products)
            SummaryTopCircles()
        }
        SummaryBottomSection(cartDetails.totalPriceAfterDiscount)
    }
}

@Composable
private fun SummaryItemsList(
   cartDetails: CheckoutUiState.CartDetails,
    products: LazyPagingItems<CheckoutUiState.CartItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Theme.spacing._24)
            .padding(horizontal = Theme.spacing._12),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        repeat(products.itemCount) { index ->
            val cartItem = products[index]
            if (cartItem != null) {
                CheckoutProductItem(cartItem = cartItem)
            }
        }
        CheckoutDiscountItem(
            productName = stringResource(Res.string.discount),
            discountPercentage = cartDetails.discountPercentage
        )
        CheckoutFeeItem(
            productName = stringResource(Res.string.platform_fees),
            price = cartDetails.platformFees
        )
        DashedSeparator(
            modifier = Modifier
                .padding(bottom = 11.dp)
                .padding(horizontal = 2.dp)
        )
    }
}

@Composable
private fun CheckoutDiscountItem(
    productName: String,
    discountPercentage: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = Theme.spacing._8)
                .weight(1f),
            text = productName,
            maxLines = 1,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Text(
            text = "-${discountPercentage} %",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Composable
private fun BoxScope.SummaryTopCircles() {
    CircleItem(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(end = 2.dp)
            .offset(x = -11.dp)
    )
    CircleItem(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(start = 2.dp)
            .offset(x = 11.dp)
    )
}

@Composable
private fun SummaryBottomSection(totalPrice: Double) {
    Box(modifier = Modifier.wrapContentHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 13.dp, bottom = 33.dp)
                .padding(horizontal = Theme.spacing._12)
        ) {
            CheckoutFeeItem(productName = "Total amount", price = totalPrice)
        }

        CircleRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 4.dp)
                .offset(y = 10.5.dp)
        )
    }
}

@Composable
private fun CheckoutProductItem(
    cartItem: CheckoutUiState.CartItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuantityCircle(cartItem.quantity)
        ProductName(cartItem.name)
        ProductPrice(cartItem.price)
    }
}

@Composable
private fun QuantityCircle(quantity: Int) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Theme.colorScheme.background.surface)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "${quantity}x",
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Composable
private fun RowScope.ProductName(name: String) {
    Text(
        modifier = Modifier
            .padding(start = Theme.spacing._8)
            .fillMaxWidth()
            .weight(1f),
        text = name,
        maxLines = 1,
        style = Theme.typography.label.medium,
        color = Theme.colorScheme.shadePrimary
    )
}

@Composable
private fun ProductPrice(price: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            contentDescription = stringResource(Res.string.silver_tier_icon)
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
                .padding(start = Theme.spacing._8)
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
            contentDescription = stringResource(Res.string.silver_tier_icon)

        )
    }
}

@Composable
private fun DashedSeparator(
    modifier: Modifier = Modifier
) {
    val dashColor = Theme.colorScheme.stroke
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = dashColor,
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
private fun CircleItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(21.dp)
            .clip(CircleShape)
            .background(Theme.colorScheme.background.surface)
    )
}

@Composable
private fun CircleRow(modifier: Modifier = Modifier) {
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
                CircleItem()
            }
        }
    }
}
