@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.screen.orderDetails.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount
import mena.dukan_presentation.generated.resources.ic_clock_time
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import mena.dukan_presentation.generated.resources.order_date_icon
import mena.dukan_presentation.generated.resources.platform_fees
import mena.dukan_presentation.generated.resources.product_order_image
import mena.dukan_presentation.generated.resources.silver_tc
import mena.dukan_presentation.generated.resources.silver_tier_icon
import mena.dukan_presentation.generated.resources.total_amount
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewOrderDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun OrderSummary(
    orderDate:String,
    productsInOrder: List<OrderDetailsUiState.ProductInOrderUiState>,
    discountAmount: Double,
    platformFeesAmount: Double,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    val circleColor = Theme.colorScheme.background.surface
    val circleWidth = 21.dp
    val circleHeight = 26.dp
    val circleWidthPx = with(LocalDensity.current) { circleWidth.toPx() }
    val circleHeightPx = with(LocalDensity.current) { circleHeight.toPx() }
    val gapPx = with(LocalDensity.current) { 1.dp.toPx() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(
                    topStart = Theme.radius.lg,
                    topEnd = Theme.radius.lg,
                    bottomStart = Theme.radius.sm,
                    bottomEnd = Theme.radius.sm,
                )
            )
            .drawBehind {
               drawBottomEdgeCutouts(
                   circleColor = circleColor,
                   circleWidthPx = circleWidthPx,
                   circleHeightPx = circleHeightPx,
                   gapPx = gapPx,
                   canvasWidth = size.width,
                   canvasHeight = size.height
               )
            }
    ) {
        OrderDateTime(
            orderDate  = orderDate,
            modifier = Modifier
                .padding(
                    vertical = Theme.spacing._12,
                    horizontal = Theme.spacing._12
                )
        )

        VerticalLine(
            modifier = Modifier.padding(
                horizontal = Theme.spacing._12
            )
        )

        ProductsInOrderList(
            productsInOrder = productsInOrder,
            modifier = Modifier
                .padding(
                    top = Theme.spacing._16,
                ).padding(horizontal = Theme.spacing._12)
        )

        DiscountSection(
            discountAmount = discountAmount,
            modifier = Modifier
                .padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._12)
        )

        PlatformFeesSection(
            platformFeesAmount = platformFeesAmount,
            modifier = Modifier
                .padding(
                    top = Theme.spacing._12,
                    bottom = Theme.spacing._24
                ).padding(horizontal = Theme.spacing._12)
        )

        TicketDivider()

        TotalAmountInOrder(
            totalAmount = totalAmount,
            modifier = Modifier
                .padding(
                    top = Theme.spacing._24,
                    bottom = Theme.spacing._32
                ).padding(horizontal = Theme.spacing._12)
        )
    }
}

private fun DrawScope.drawBottomEdgeCutouts(
    circleColor: Color,
    circleWidthPx: Float,
    circleHeightPx: Float,
    gapPx: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val spacePerCircle = circleWidthPx + gapPx
    val circleCount = (canvasWidth / spacePerCircle).toInt().coerceAtLeast(0)
    if (circleCount == 0) return
    val totalWidthNeeded = circleCount * circleWidthPx + (circleCount - 1) * gapPx
    val circlesStartX = (canvasWidth - totalWidthNeeded) / 2f
    val circleYPosition = canvasHeight - circleHeightPx / 2f
    repeat(circleCount) { index ->
        val circleXPosition = circlesStartX + index * (circleWidthPx + gapPx)
        drawOval(
            color = circleColor,
            topLeft = Offset(circleXPosition, circleYPosition),
            size = Size(circleWidthPx, circleHeightPx)
        )
    }
}

@Composable
private fun OrderDateTime(
    orderDate: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_clock_time),
            contentDescription = stringResource(Res.string.order_date_icon),
            tint = Theme.colorScheme.shadePrimary,
        )
        Text(
            text = orderDate,
            maxLines = 1,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Composable
private fun VerticalLine(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .border(1.dp, Theme.colorScheme.stroke)
    )
}

@Composable
private fun ProductsInOrderList(
    productsInOrder: List<OrderDetailsUiState.ProductInOrderUiState>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
    ) {
        repeat(productsInOrder.size) { index ->
            productsInOrder[index].let { product ->
                ProductInOrderItem(
                    name = product.name,
                    totalPrice = product.totalPrice,
                    quantity = product.quantity,
                    imageUrl = product.imageUrl,
                )
            }
        }
    }
}
@Composable
private fun DiscountSection(
    discountAmount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.discount),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary
        )
        Text(
            text = "-$discountAmount%",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
    }
}
@Composable
private fun PlatformFeesSection(
    platformFeesAmount: Double,
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.platform_fees),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary
        )
        Text(
            text = "$platformFeesAmount%",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Composable
private fun ProductInOrderItem(
    name: String,
    totalPrice: Double,
    quantity: Int,
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        QuantityCircleIcon(quantity = quantity)
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(Res.string.product_order_image),
            placeholder = painterResource(resource = Res.drawable.ic_no_image_loaded),
            error = painterResource(resource = Res.drawable.ic_no_image_loaded),
            modifier = Modifier
                .size(40.dp)
                .padding(end = Theme.spacing._8)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = name,
            maxLines = 1,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Text(
                text = totalPrice.toString(),
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1
            )
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(Res.drawable.silver_tc),
                contentDescription = stringResource(Res.string.silver_tier_icon)
            )
        }
    }
}

@Composable
private fun QuantityCircleIcon(
    quantity: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
private fun TicketDivider(
    modifier: Modifier = Modifier
) {
    val dashColor = Theme.colorScheme.stroke
    val circleColor = Theme.colorScheme.background.surface
    val circleWidth = 21.dp
    val circleHeight = 26.dp
    val circleWidthPx = with(LocalDensity.current) { circleWidth.toPx() }
    val circleHeightPx = with(LocalDensity.current) { circleHeight.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val circleHorizontalPaddingOffset = 30f
        val circleVerticalPaddingOffset = 7f
        val lineStartPaddingOffset = 20f
        val lineEndPaddingOffset = 30f
        val rightCircleX = (size.width - circleWidthPx)
        val circleY = (size.height - circleHeightPx / 2f) + circleVerticalPaddingOffset
        drawOval(
            color = circleColor,
            topLeft = Offset(-circleHorizontalPaddingOffset, circleY),
            size = Size(circleWidthPx, circleHeightPx)
        )
        drawLine(
            color = dashColor,
            start = Offset(circleWidthPx - lineStartPaddingOffset, size.height / 2),
            end = Offset(size.width - lineEndPaddingOffset, size.height / 2),
            strokeWidth = 0.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
                0f
            )
        )
        drawOval(
            color = circleColor,
            topLeft = Offset(rightCircleX + circleHorizontalPaddingOffset, circleY),
            size = Size(circleWidthPx, circleHeightPx)
        )
    }
}

@Composable
private fun TotalAmountInOrder(
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.total_amount),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Text(
                text = totalAmount.toString(),
                maxLines = 1,
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary,
            )
            Icon(
                modifier = Modifier
                    .size(20.dp),
                painter = painterResource(Res.drawable.silver_tc),
                contentDescription = stringResource(Res.string.silver_tier_icon)
            )
        }
    }
}

@Preview
@Composable
private fun OrderDetailsScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(color = Theme.colorScheme.background.surface)
        ) {
            OrderSummary(
                orderDate = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.orderDate,
                productsInOrder = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.productInOrder,
                discountAmount = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.discount,
                platformFeesAmount = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.platformFees,
                totalAmount = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.totalAmount,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}