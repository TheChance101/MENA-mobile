package net.thechance.mena.wallet.presentation.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
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
import kotlinx.datetime.number
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.pick
import mena.wallet_presentation.generated.resources.pick_start_date
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.utils.AppMonth
import net.thechance.mena.wallet.presentation.utils.getNumberOfDaysInMonth
import net.thechance.mena.wallet.presentation.utils.today
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Composable
fun ScaffoldScope.DatePickerBottomSheet(
    isVisible: Boolean,
    title: String = stringResource(Res.string.pick_start_date),
    minYear: Int = 2025,
    maxYear: Int = LocalDate.today().year,
    defaultSelectedDate: LocalDate = LocalDate.today().date,
    onPickClick: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomSheet(
        isVisible = isVisible,
        skipPartiallyExpanded = true,
        onDismissRequest = onDismiss,
        modifier = modifier.navigationBarsPadding(),
        sheetContent = {
            DatePickerBottomSheetContent(
                title = title,
                minYear = minYear,
                maxYear = maxYear,
                onPickClick = onPickClick,
                selectedDate = defaultSelectedDate,
                onDismiss = onDismiss
            )
        }
    )
}


@OptIn(ExperimentalTime::class)
@Composable
private fun DatePickerBottomSheetContent(
    title: String = stringResource(Res.string.pick_start_date),
    minYear: Int = 2025,
    maxYear: Int = LocalDate.today().year,
    onPickClick: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
) {
    val today =
        remember { LocalDate.today().date }

    val yearPagerState = rememberPagerState(
        initialPage = (selectedDate.year) - minYear,
        pageCount = { maxYear - minYear + 1 }
    )

    val monthPagerState = rememberPagerState(
        initialPage = (selectedDate.month.number) - 1,
        pageCount = { if (minYear + yearPagerState.currentPage == today.year) today.month.number else 12 }
    )

    val currentMonth = monthPagerState.currentPage + 1
    val currentYear = minYear + yearPagerState.currentPage
    val daysInMonth = remember(currentMonth, currentYear) {
        val totalDays = getNumberOfDaysInMonth(currentYear, currentMonth)
        if (currentYear == today.year && currentMonth == today.month.number) {
            today.day
        } else {
            totalDays
        }
    }

    val dayPagerState = rememberPagerState(
        initialPage = (selectedDate.day) - 1,
        pageCount = { daysInMonth }
    )

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

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .background(Theme.colorScheme.background.surface)
            .fillMaxWidth()
            .padding(16.dp)
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
            daysInMonth = daysInMonth,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        PrimaryButton(
            text = stringResource(Res.string.pick),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 13.dp),
            onClick = {
                val day = dayPagerState.currentPage + 1
                val month = monthPagerState.currentPage + 1
                val year = minYear + yearPagerState.currentPage
                onPickClick(LocalDate(year, month, day))
            },
            modifier = Modifier
                .padding(bottom = 8.dp)
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
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                    .clickable { onDismiss() }
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
    daysInMonth: Int,
    modifier: Modifier = Modifier
) {
    val days = remember(daysInMonth) {
        (1..daysInMonth).map { it.toString().padStart(2, '0') }
    }

    var rowWidth by remember { mutableStateOf(0) }

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
                modifier = Modifier.padding(end = 32.dp)
            )

            VerticalPicker(
                state = dayPagerState,
                options = days,
                modifier = Modifier.padding(end = 52.dp)
            )

            VerticalPicker(
                state = yearPagerState,
                options = (minYear..maxYear).map { it.toString() },
                isYearPicker = true
            )
        }
    }
}


@Composable
private fun ChoiceIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.sm))
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
        DatePickerBottomSheetContent(
            onPickClick = { },
            onDismiss = {},
            minYear = 2021,
            maxYear = 2025,
            selectedDate = LocalDate(2023, 1, 1)
        )
    }
}