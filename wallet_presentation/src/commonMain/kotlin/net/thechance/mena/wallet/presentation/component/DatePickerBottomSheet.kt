package net.thechance.mena.wallet.presentation.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.pick
import mena.wallet_presentation.generated.resources.pick_start_date
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.utils.AppMonth
import net.thechance.mena.wallet.presentation.utils.getNumberOfDaysInMonth
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DatePickerBottomSheet(
    title: String = stringResource(Res.string.pick_start_date),
    minYear: Int = 2000,
    maxYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    onPickClick: (Int, Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val monthPagerState = rememberPagerState(initialPage = 0, pageCount = { 12 })
    val yearPagerState = rememberPagerState(
        initialPage = maxYear - minYear,
        pageCount = { maxYear - minYear + 1 }
    )

    val currentMonth = monthPagerState.currentPage + 1
    val currentYear = yearPagerState.currentPage + minYear
    val daysInMonth = remember(currentMonth, currentYear) {
        getNumberOfDaysInMonth(currentYear, currentMonth)
    }

    val dayPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { daysInMonth }
    )

    LaunchedEffect(daysInMonth) {
        if (dayPagerState.currentPage >= daysInMonth) {
            dayPagerState.scrollToPage(daysInMonth - 1)
        }
    }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .background(Theme.colorScheme.background.surface)
            .fillMaxWidth()
            .padding(Theme.spacing._16)
    ) {

        BottomSheetTopBar(
            onDismiss = onDismiss,
            title = title
        )

        WheelDatePicker(
            dayPagerState = dayPagerState,
            monthPagerState = monthPagerState,
            yearPagerState = yearPagerState,
            minYear = minYear,
            maxYear = maxYear,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = Theme.spacing._16)
                .align(Alignment.CenterHorizontally)
        )

        PrimaryButton(
            text = stringResource(Res.string.pick),
            contentPadding = PaddingValues(horizontal = Theme.spacing._24, vertical = 13.dp),
            onClick = {
                val day = dayPagerState.currentPage + 1
                val month = monthPagerState.currentPage + 1
                val year = minYear + yearPagerState.currentPage
                onPickClick(day, month, year)
            },
            modifier = Modifier
                .padding(bottom = Theme.spacing._8)
                .fillMaxWidth()
        )

    }
}

@Composable
private fun BottomSheetTopBar(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._12),
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingValues(horizontal = 0.dp, vertical = 0.dp))
    ) {
        Box(
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Theme.colorScheme.background.surfaceLow,
                        RoundedCornerShape(Theme.radius.md)
                    )
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .clickable { onDismiss }
                    .padding(10.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = stringResource(Res.string.back_button)
                )
            }
        }
        Text(
            text = title,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.small,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WheelDatePicker(
    minYear: Int,
    maxYear: Int,
    dayPagerState: PagerState,
    monthPagerState: PagerState,
    yearPagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val days = remember(monthPagerState.currentPage, yearPagerState.currentPage) {
        val month = monthPagerState.currentPage + 1
        val year = yearPagerState.currentPage + minYear
        (1..getNumberOfDaysInMonth(year, month))
            .map { number -> number.toString().padStart(2, '0') }
    }

    LaunchedEffect(days.size) {
        if (dayPagerState.currentPage >= days.size) {
            dayPagerState.scrollToPage(days.lastIndex)
        }
    }

    var rowWidth by remember { mutableStateOf(0) }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {

        ChoiceIndicator(
            modifier = Modifier
                .width(with(LocalDensity.current) { rowWidth.toDp() })
                .fillMaxHeight()
        )

        Row(
            modifier = Modifier
                .onSizeChanged { rowWidth = it.width }
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(27.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VerticalPicker(
                state = dayPagerState,
                options = days
            )

            VerticalPicker(
                state = monthPagerState,
                options = AppMonth.entries.map { stringResource(it.res) }
            )

            VerticalPicker(
                state = yearPagerState,
                options = (minYear..maxYear).map { it.toString() }
            )

        }
    }
}

@Composable
private fun ChoiceIndicator(modifier: Modifier = Modifier) {
    val borderColor = Theme.colorScheme.stroke
    Canvas(
        modifier = modifier
    ) {
        val centerY = size.height / 2f
        val itemHeight = 40.dp.toPx()
        val topBorderY = centerY - (itemHeight / 2f)
        val bottomBorderY = centerY + (itemHeight / 2f)

        val strokeWidth = 1.dp.toPx()

        drawLine(
            color = borderColor,
            start = Offset(0f, topBorderY),
            end = Offset(size.width, topBorderY),
            strokeWidth = strokeWidth
        )

        drawLine(
            color = borderColor,
            start = Offset(0f, bottomBorderY),
            end = Offset(size.width, bottomBorderY),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
private fun VerticalPicker(
    state: PagerState,
    options: List<String>,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val baseStyle = Theme.typography.body.large

    val maxWidth = remember(options) {
        options.maxOf { option ->
            textMeasurer.measure(
                text = option,
                style = baseStyle
            ).size.width
        }
    }

    Box(
        modifier = modifier
            .width(with(LocalDensity.current) { maxWidth.toDp() })
            .fillMaxHeight()
    ) {
        VerticalPager(
            state = state,
            modifier = Modifier.fillMaxHeight().wrapContentWidth(),
            beyondViewportPageCount = 5,
            horizontalAlignment = Alignment.CenterHorizontally,
            pageSpacing = 4.dp,
            contentPadding = PaddingValues(vertical = 80.dp),
            pageSize = PageSize.Fixed(40.dp),
            flingBehavior = PagerDefaults.flingBehavior(
                state = state,
                pagerSnapDistance = PagerSnapDistance.atMost(1),
                snapAnimationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
            )
        ) { page ->
            val pageOffset by remember {
                derivedStateOf {
                    ((state.currentPage - page) + state.currentPageOffsetFraction).absoluteValue
                }
            }
            val alpha = (1f - (pageOffset * 0.5f)).coerceAtLeast(0.1f)

            val baseStyle = Theme.typography.body.large
            val distance = abs(state.currentPage - page)
            val fontSize = (baseStyle.fontSize.value - distance).coerceAtLeast(14f).sp

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = options.getOrNull(page) ?: "",
                    style = baseStyle.copy(fontSize = fontSize),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = Theme.colorScheme.shadePrimary.copy(alpha = alpha)
                )
            }
        }
    }
}

@Preview
@Composable
private fun DatePickerBottomSheetContentPreview() {

    MenaTheme {
        DatePickerBottomSheet(
            onPickClick = { _, _, _ -> },
            onDismiss = {},
            minYear = 2021,
            maxYear = 2025
        )
    }
}