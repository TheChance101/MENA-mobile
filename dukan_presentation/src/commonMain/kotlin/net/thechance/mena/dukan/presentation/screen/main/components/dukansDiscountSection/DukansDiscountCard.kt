@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.screen.main.components.dukansDiscountSection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_discount_details
import mena.dukan_presentation.generated.resources.dukan_discount_title
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.ic_arrow_right
import mena.dukan_presentation.generated.resources.shop_now
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.modifiers.fillWidthOfParent
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun DukansDiscountSection(
    state: List<MainScreenUiState.DukanTopDiscount>,
    onClick: (dukanId: Uuid) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = { state.size })

    Box(modifier.fillWidthOfParent(parentPadding = Theme.spacing._16)) {
        BannerItem(
            state = state,
            pagerState = pagerState,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
                .padding(bottom = Theme.spacing._8 + Theme.spacing._2)
        )

        Indicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

}

@Composable
private fun BannerItem(
    state: List<MainScreenUiState.DukanTopDiscount>,
    pagerState: PagerState,
    onClick: (dukanId: Uuid) -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(pagerState) {
        while (true) {
            if (!pagerState.isScrollInProgress && state.size > 1) {
                delay(1500)
                val nextPage = (pagerState.currentPage + 1) % state.size
                pagerState.animateScrollToPage(nextPage)
            } else {
                delay(100)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
            .height(184.dp)
            .clip(SquircleShape(Theme.radius.lg))
    ) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            key = { page -> state[page].id },
            modifier = Modifier.fillMaxSize()
        ) { page ->

            DukanDiscountImagesAndText(
                state = state,
                page = page,
                modifier = Modifier.fillMaxSize()
            )
        }

        ShopNowButton(
            modifier = Modifier.align(Alignment.BottomStart)
                .padding(start = Theme.spacing._12, bottom = 19.dp),
            onClick = { onClick(state[pagerState.currentPage].id) })
    }
}


@Composable
private fun DukanDiscountImagesAndText(
    modifier: Modifier = Modifier,
    state: List<MainScreenUiState.DukanTopDiscount>,
    page: Int
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = state[page].imageUrl,
            contentDescription = stringResource(Res.string.dukan_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
        DukanDiscountText(
            dukanDiscount = state[page].discount,
            modifier = Modifier.padding(bottom = Theme.spacing._8)
        )
    }
}

@Composable
private fun DukanDiscountText(
    dukanDiscount: Int,
    modifier: Modifier = Modifier
) {

    Column(
        modifier.fillMaxSize()
            .padding(start = Theme.spacing._12, end = Theme.spacing._12),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.dukan_discount_title, dukanDiscount),
            style = Theme.typography.title.large,
            color = Color.White,
            modifier = Modifier.padding(bottom = Theme.spacing._4)
        )

        Text(
            text = stringResource(Res.string.dukan_discount_details),
            style = Theme.typography.label.small,
            color = Color.White,
            modifier = Modifier.padding(bottom = Theme.spacing._8)
        )

    }
}

@Composable
private fun ShopNowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.primary.onPrimary)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            ).padding(
                horizontal = Theme.spacing._12,
                vertical = Theme.spacing._4
            ),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.shop_now),
            color = Theme.colorScheme.primary.primary,
            style = Theme.typography.label.medium,
        )
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Theme.colorScheme.primary.primary
        )
    }
}

@Preview
@Composable
private fun DukanDiscountPreview() {
    MenaTheme {
        DukansDiscountSection(
            state = listOf(),
            onClick = {}
        )
    }
}