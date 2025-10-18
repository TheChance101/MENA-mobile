package net.thechance.mena.identity.presentation.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.util.AppMonth
import net.thechance.mena.identity.presentation.util.getNumberOfDaysInMonth
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun WheelDatePicker(
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier,
    minYear: Int = 1920,
    maxYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    onDateChange: (day: Int, month: Int, year: Int) -> Unit,
) {
    if (selectedDate == null)
        return

    val monthPagerState = rememberPagerState(
        initialPage = (selectedDate.month.number) - 1, pageCount = { 12 }
    )

    val yearPagerState = rememberPagerState(
        initialPage = (selectedDate.year) - minYear,
        pageCount = { maxYear - minYear + 1 }
    )

    val currentMonth = monthPagerState.currentPage + 1
    val currentYear = minYear + yearPagerState.currentPage
    val daysInMonth = remember(currentMonth, currentYear) {
        getNumberOfDaysInMonth(currentYear, currentMonth)
    }

    val dayPagerState = rememberPagerState(
        initialPage = (selectedDate.day) - 1,
        pageCount = { daysInMonth }
    )

    val days = remember(daysInMonth) {
        (1..daysInMonth).map { it.toString().padStart(2, '0') }
    }

    var rowWidth by remember { mutableStateOf(0) }

    LaunchedEffect(selectedDate) {
        val targetYearPage = selectedDate.year - minYear
        val targetMonthPage = selectedDate.month.number - 1
        val targetDayPage = selectedDate.day - 1

        coroutineScope {
            if (yearPagerState.currentPage != targetYearPage) {
                launch { yearPagerState.scrollToPage(targetYearPage) }
            }
            if (monthPagerState.currentPage != targetMonthPage) {
                launch { monthPagerState.scrollToPage(targetMonthPage) }
            }
            if (dayPagerState.currentPage != targetDayPage) {
                launch { dayPagerState.scrollToPage(targetDayPage) }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(244.dp),
        contentAlignment = Alignment.Center
    ) {
        ChoiceIndicator(
            modifier = Modifier
                .width(with(LocalDensity.current) { rowWidth.toDp() + 20.dp })
                .align(Alignment.Center)
        )

        Row(
            modifier = Modifier
                .onSizeChanged { rowWidth = it.width }
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VerticalPicker(
                state = monthPagerState,
                options = AppMonth.entries.map { stringResource(it.res) },
                modifier = Modifier.padding(end = Theme.spacing._32),
            ) {
                val day = dayPagerState.currentPage + 1
                val month = monthPagerState.currentPage + 1
                val year = minYear + yearPagerState.currentPage
                onDateChange(day, month, year)
            }

            VerticalPicker(
                state = dayPagerState,
                options = days,
                modifier = Modifier.padding(end = 52.dp)
            ) {
                val day = dayPagerState.currentPage + 1
                val month = monthPagerState.currentPage + 1
                val year = minYear + yearPagerState.currentPage
                onDateChange(day, month, year)
            }

            VerticalPicker(
                state = yearPagerState,
                options = (minYear..maxYear).map { it.toString() },
                isYearPicker = true
            ) {
                val day = dayPagerState.currentPage + 1
                val month = monthPagerState.currentPage + 1
                val year = minYear + yearPagerState.currentPage
                onDateChange(day, month, year)
            }
        }
    }
}


@Composable
private fun ChoiceIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(SquircleShape(Theme.radius.md))
            .height(28.dp)
            .background(Theme.colorScheme.background.surfaceHigh)
    )
}

@Composable
private fun VerticalPicker(
    state: PagerState,
    options: List<String>,
    isYearPicker: Boolean = false,
    modifier: Modifier = Modifier,
    onDateChange: () -> Unit
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
            contentPadding = PaddingValues(vertical = 108.dp),
            pageSize = PageSize.Fixed(28.dp),
            flingBehavior = PagerDefaults.flingBehavior(
                state = state,
                pagerSnapDistance = PagerSnapDistance.atMost(1),
                snapAnimationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
            )
        ) { page ->
            val distance = (page - state.currentPage).absoluteValue

            val alpha = when (distance) {
                0 -> 1f
                1 -> 0.9f
                2 -> 0.5f
                else -> 0.2f
            }

            onDateChange()

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .graphicsLayer {
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = options[page],
                    style = Theme.typography.label.medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = if (isYearPicker) TextAlign.End else TextAlign.Start,
                    color = Theme.colorScheme.shadePrimary
                )
            }
        }
    }
}

@Preview
@Composable
private fun DatePickerBottomSheetContentPreview() {
    MenaTheme {
        WheelDatePicker(
            selectedDate = LocalDate(2023, 1, 1),
            onDateChange = { day, month, year -> },
        )
    }
}