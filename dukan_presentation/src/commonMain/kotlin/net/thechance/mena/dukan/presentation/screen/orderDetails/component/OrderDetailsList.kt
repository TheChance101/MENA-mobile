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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_clock_time
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import mena.dukan_presentation.generated.resources.silver_tc
import mena.dukan_presentation.generated.resources.silver_tier_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OrderDetailsList(
    modifier: Modifier = Modifier
) {
    val circleColor = Theme.colorScheme.background.surface
    val circleWidth = 21.dp
    val circleHeight = 26.dp
    val circleWidthPx = with(LocalDensity.current) { circleWidth.toPx() }
    val circleHeightPx = with(LocalDensity.current) { circleHeight.toPx() }
    val gapPx = with(LocalDensity.current) { 2.dp.toPx() }
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
                val spacePerCircle = circleWidthPx + gapPx
                if (spacePerCircle <= 0f) return@drawBehind
                val circleCount = (size.width / spacePerCircle).toInt().coerceAtLeast(0)
                if (circleCount == 0) return@drawBehind
                val totalWidthNeeded = circleCount * circleWidthPx + (circleCount - 1) * gapPx
                val startX = (size.width - totalWidthNeeded) / 2f
                val y = size.height - circleHeightPx / 2f
                repeat(circleCount) { index ->
                    val x = startX + index * (circleWidthPx + gapPx)
                    drawOval(
                        color = circleColor,
                        topLeft = Offset(x, y),
                        size = Size(circleWidthPx, circleHeightPx)
                    )
                }
            },
    ) {
        OrderDateTime(
            modifier = Modifier
                .padding(vertical = Theme.spacing._12)
        )
        VerticalLine()
        ProductsInOrderList(
            modifier = Modifier.padding(
                top = Theme.spacing._16,
                bottom = Theme.spacing._24
            )
        )
        DashedSeparator(
            modifier = Modifier
                .padding(horizontal = 2.dp)
        )
        TotalAmountInOrder(
            totalAmount = 250.0,
            modifier = Modifier
                .padding(
                    top = Theme.spacing._24,
                    bottom = Theme.spacing._32
                )
        )
    }
}

@Composable
private fun OrderDateTime(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_clock_time),
            contentDescription = "Order Date",
            tint = Theme.colorScheme.shadePrimary,
        )
        Text(
            text = "12/04/2025",
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
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 304.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(140.dp, 200.dp),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
    ) {
        items(10) {
            ProductInOrderItem(
                title = "Product Name Here$it",
                price = 25.0,
                quantity = it + 1,
                imageUrl = "",
            )
        }
    }
}

@Composable
private fun ProductInOrderItem(
    title: String,
    price: Double,
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
            contentDescription = "Product of Order Image",
            placeholder = painterResource(resource = Res.drawable.ic_no_image_loaded),
            error = painterResource(resource = Res.drawable.ic_no_image_loaded),
            modifier = Modifier.size(40.dp)
        )
        Text(
            modifier = Modifier
                .padding(start = Theme.spacing._8)
                .fillMaxWidth()
                .weight(1f),
            text = title,
            maxLines = 1,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Text(
                text = price.toString(),
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary
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

@Composable
private fun QuantityCircleIcon(quantity: Int) {
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
fun TotalAmountInOrder(
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Total amount",
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Row {
            Text(
                text = totalAmount.toString(),
                maxLines = 1,
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary,
            )
            Icon(
                modifier = Modifier
                    .padding(start = Theme.spacing._4)
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
        OrderDetailsList()
    }
}