package net.thechance.mena.faith.presentation.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.datePicker.WheelDatePicker
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HijriDatePicker(
    selectedDay: Int,
    selectedMonth: Int,
    selectedYear: Int,
    modifier: Modifier = Modifier,
    minYear: Int = 1343,
    maxYear: Int = 1500,
    useShortMonthNames: Boolean? = null,
    onDateChange: (day: Int, month: Int, year: Int) -> Unit,
) {
    var containerWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    val shouldUseShortNames = remember(useShortMonthNames, containerWidth) {
        useShortMonthNames ?: (with(density) { containerWidth.toDp() } < 340.dp)
    }

    val hijriMonthNames = remember(shouldUseShortNames) {
        if (shouldUseShortNames) getHijriMonthNamesShort() else getHijriMonthNames()
    }
    val yearList = remember(minYear, maxYear) {
        (minYear..maxYear).map { it.toString() }
    }

    val currentMonthState = remember { mutableStateOf(selectedMonth) }
    val currentYearState = remember { mutableStateOf(selectedYear) }
    val currentMonth = currentMonthState.value
    val currentYear = currentYearState.value

    val daysList = remember(currentMonth, currentYear) {
        createHijriDaysList(currentMonth, currentYear)
    }

    val selectedIndices = remember(selectedDay, selectedMonth, selectedYear, daysList.size) {
        calculateHijriSelectionIndices(
            day = selectedDay,
            month = selectedMonth,
            year = selectedYear,
            daysCount = daysList.size,
            minYear = minYear
        )
    }

    LaunchedEffect(selectedMonth, selectedYear) {
        currentMonthState.value = selectedMonth
        currentYearState.value = selectedYear
    }

    WheelDatePicker(
        selectedDayIndex = selectedIndices.dayIndex,
        selectedMonthIndex = selectedIndices.monthIndex,
        selectedYearIndex = selectedIndices.yearIndex,
        days = daysList,
        months = hijriMonthNames,
        years = yearList,
        modifier = modifier.onSizeChanged { size ->
            containerWidth = size.width
        },
        onDateChange = { dayIndex, monthIndex, yearIndex ->
            handleHijriWheelDateChange(
                dayIndex = dayIndex,
                monthIndex = monthIndex,
                yearIndex = yearIndex,
                minYear = minYear,
                onMonthYearUpdate = { month, year ->
                    currentMonthState.value = month
                    currentYearState.value = year
                },
                onDateChange = onDateChange
            )
        }
    )
}

private fun getHijriMonthNames(): List<String> {
    return listOf(
        "Muharram",
        "Safar",
        "Rabi' al-Awwal",
        "Rabi' al-Thani",
        "Jumada al-Awwal",
        "Jumada al-Thani",
        "Rajab",
        "Sha'ban",
        "Ramadan",
        "Shawwal",
        "Dhul-Qi'dah",
        "Dhul-Hijjah"
    )
}

private fun getHijriMonthNamesShort(): List<String> {
    return listOf(
        "Muharram",
        "Safar",
        "Rabi' I",
        "Rabi' II",
        "Jumada I",
        "Jumada II",
        "Rajab",
        "Sha'ban",
        "Ramadan",
        "Shawwal",
        "Dhul-Qi'dah",
        "Dhul-Hijjah"
    )
}

private fun createHijriDaysList(month: Int, year: Int): List<String> {
    val daysCount = getHijriDaysInMonth(month, year)
    return (1..daysCount).map { day ->
        day.toString().padStart(2, '0')
    }
}

private fun getHijriDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 9, 11 -> 30
        2, 4, 6, 8, 10 -> 29
        12 -> if (isHijriLeapYear(year)) 30 else 29
        else -> 30
    }
}

private fun isHijriLeapYear(year: Int): Boolean {
    return ((year * 11 + 14) % 30) < 11
}

private data class HijriSelectionIndices(
    val dayIndex: Int,
    val monthIndex: Int,
    val yearIndex: Int
)

private fun calculateHijriSelectionIndices(
    day: Int,
    month: Int,
    year: Int,
    daysCount: Int,
    minYear: Int
): HijriSelectionIndices {
    val dayIndex = day.coerceIn(1, daysCount) - 1
    val monthIndex = month - 1
    val yearIndex = year - minYear

    return HijriSelectionIndices(
        dayIndex = dayIndex,
        monthIndex = monthIndex,
        yearIndex = yearIndex
    )
}

private fun convertHijriMonthIndexToValue(monthIndex: Int): Int = monthIndex + 1

private fun convertHijriYearIndexToValue(yearIndex: Int, minYear: Int): Int = minYear + yearIndex

private fun convertHijriDayIndexToValue(dayIndex: Int): Int = dayIndex + 1

private fun handleHijriWheelDateChange(
    dayIndex: Int,
    monthIndex: Int,
    yearIndex: Int,
    minYear: Int,
    onMonthYearUpdate: (month: Int, year: Int) -> Unit,
    onDateChange: (day: Int, month: Int, year: Int) -> Unit
) {
    val month = convertHijriMonthIndexToValue(monthIndex)
    val year = convertHijriYearIndexToValue(yearIndex, minYear)

    onMonthYearUpdate(month, year)

    val daysInMonth = getHijriDaysInMonth(month, year)
    val day = convertHijriDayIndexToValue(dayIndex).coerceIn(1, daysInMonth)

    onDateChange(day, month, year)
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun HijriDatePickerPreviewNarrow() {
    MenaTheme {
        Box(
            modifier = Modifier
                .width(300.dp)
                .background(Theme.colorScheme.background.surfaceLow)
        ) {
            HijriDatePicker(
                selectedDay = 15,
                selectedMonth = 9,
                selectedYear = 1446,
                minYear = 1343,
                maxYear = 1500,
                onDateChange = { _, _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun HijriDatePickerPreviewWide() {
    MenaTheme {
        Box(
            modifier = Modifier
                .width(350.dp)
                .background(Theme.colorScheme.background.surfaceLow)
        ) {
            HijriDatePicker(
                selectedDay = 15,
                selectedMonth = 9,
                selectedYear = 1446,
                minYear = 1343,
                maxYear = 1500,
                onDateChange = { _, _, _ -> }
            )
        }
    }
}
