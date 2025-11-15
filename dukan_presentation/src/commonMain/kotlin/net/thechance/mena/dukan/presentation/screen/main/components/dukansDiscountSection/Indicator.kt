package net.thechance.mena.dukan.presentation.screen.main.components.dukansDiscountSection

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.math.abs

@Composable
fun Indicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    dotWidth: Int = 5
) {

    val pageCount by remember { mutableStateOf(minOf(pagerState.pageCount,5)) }
    val activeIndex by remember(pagerState.currentPage) { mutableStateOf(pagerState.currentPage % pageCount) }

    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(Theme.radius.full))
            .background(color = Theme.colorScheme.background.surfaceLow)
            .border(
                width = 3.dp,
                color = Theme.colorScheme.background.surface,
                shape = RoundedCornerShape(Theme.radius.full)
            )
            .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._8),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._2)
        ) {
            repeat(pageCount) { index ->

                val pageOffset = (index - activeIndex) + pagerState.currentPageOffsetFraction

                val dotWidth = when {
                    pageOffset > 1f || pageOffset < -1f -> dotWidth.dp
                    else -> {
                        val animatedFraction = 1f - abs(pageOffset)
                        dotWidth.dp + (15.dp * animatedFraction)
                    }
                }

                val dotColor by animateColorAsState(
                    targetValue = if (activeIndex == index) Theme.colorScheme.primary.primary else Theme.colorScheme.stroke,
                )

                Box(
                    modifier = Modifier.height(5.dp)
                        .width(dotWidth)
                        .clip(RoundedCornerShape(Theme.radius.full))
                        .background(dotColor)
                )
            }
        }
    }
}