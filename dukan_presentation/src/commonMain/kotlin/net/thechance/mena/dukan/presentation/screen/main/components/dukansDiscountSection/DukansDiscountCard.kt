package net.thechance.mena.dukan.presentation.screen.main.components.dukansDiscountSection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.modifiers.fillWidthOfParent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukansDiscountSection(
    dukanDiscountImages: List<String>,
    dukanDiscount: Int,
    dukanId: String,
    dukanColor: Color,
    onClick: (dukanId: String) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = { dukanDiscountImages.size })

    Box(modifier.fillWidthOfParent(parentPadding = Theme.spacing._16)) {
        DukanDiscountImagesAndText(
            dukanDiscountImages = dukanDiscountImages,
            pagerState = pagerState,
            dukanDiscount = dukanDiscount,
            dukanId = dukanId,
            onClick = onClick,
            dukanColor = dukanColor,
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
private fun DukanDiscountImagesAndText(
    dukanDiscountImages: List<String>,
    pagerState: PagerState,
    dukanDiscount: Int,
    dukanId: String,
    dukanColor: Color,
    onClick: (dukanId: String) -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(pagerState) {
        while (dukanDiscountImages.size > 1) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % dukanDiscountImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
            .height(184.dp)
            .clip(RoundedCornerShape(Theme.radius.lg))
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val model = dukanDiscountImages[page]

            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = model,
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
                                    dukanColor.copy(alpha = 0.8f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                DukanDiscountText(
                    dukanDiscount = dukanDiscount,
                    dukanId = dukanId,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
private fun DukanDiscountText(
    dukanDiscount: Int,
    dukanId: String,
    onClick: (dukanId: String) -> Unit
) {

    Column(
        Modifier.fillMaxSize()
            .padding(bottom = 19.dp, start = Theme.spacing._12, end = Theme.spacing._12),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = "$dukanDiscount% DISCOUNT\nToday special",
            style = Theme.typography.title.large,
            color = Theme.colorScheme.primary.onPrimary,
            modifier = Modifier.padding(bottom = Theme.spacing._4)
        )

        Text(
            text = "Get discount for every order, only valid for today.",
            style = Theme.typography.label.small,
            color = Theme.colorScheme.primary.onPrimary,
            modifier = Modifier.padding(bottom = Theme.spacing._8)
        )

        PrimaryButton(
            text = "Shop Now",
            onClick = { onClick(dukanId) },
            trailingIcon = painterResource(Res.drawable.ic_arrow_right),
            iconStartPadding = Theme.spacing._2,
            containerColor = Theme.colorScheme.primary.onPrimary,
            contentColor = Theme.colorScheme.primary.primary,
            contentPadding = PaddingValues(
                horizontal = Theme.spacing._12,
                vertical = Theme.spacing._4
            ),
            shape = RoundedCornerShape(Theme.radius.full)
        )
    }
}

@Preview
@Composable
private fun DukanDiscountPreview() {
    MenaTheme {
        DukansDiscountSection(
            dukanDiscountImages = listOf(),
            dukanDiscount = 10,
            dukanId = "1",
            dukanColor = Color.Red,
            onClick = {}
        )
    }
}