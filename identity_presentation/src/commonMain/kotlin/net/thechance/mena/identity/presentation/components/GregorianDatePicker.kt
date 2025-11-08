package net.thechance.mena.identity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.designsystem.presentation.component.datePicker.WheelDatePicker
import net.thechance.mena.identity.presentation.util.getDefaultMonthNames
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun GregorianDatePicker(
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier,
    minYear: Int = 1920,
    maxYear: Int = 2024,
    onDateChange: (day: Int, month: Int, year: Int) -> Unit,
) {
    val dateToUse = getEffectiveDate(selectedDate)
    val monthNames = getDefaultMonthNames()
    val yearList = createYearList(minYear, maxYear)
    val (currentMonthState, currentYearState) = rememberMonthYearState(dateToUse)
    val currentMonth = currentMonthState.value
    val currentYear = currentYearState.value

    val daysList = createDaysList(currentMonth, currentYear)
    val selectedIndices = remember(dateToUse, currentMonth, currentYear, daysList.size) {
        calculateSelectionIndices(
            date = dateToUse,
            month = currentMonth,
            year = currentYear,
            daysCount = daysList.size,
            minYear = minYear
        )
    }

    WheelDatePicker(
        selectedDayIndex = selectedIndices.dayIndex,
        selectedMonthIndex = selectedIndices.monthIndex,
        selectedYearIndex = selectedIndices.yearIndex,
        days = daysList,
        months = monthNames,
        years = yearList,
        modifier = modifier,
        onDateChange = { dayIndex, monthIndex, yearIndex ->
            handleWheelDateChange(
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

@OptIn(ExperimentalTime::class)
private fun getEffectiveDate(selectedDate: LocalDate?): LocalDate {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return selectedDate ?: today
}

@Composable
private fun createYearList(minYear: Int, maxYear: Int): List<String> {
    return remember(minYear, maxYear) {
        (minYear..maxYear).map { it.toString() }
    }
}

@Composable
private fun rememberMonthYearState(
    initialDate: LocalDate
): Pair<MutableState<Int>, MutableState<Int>> {
    val dateKey = "${initialDate.year}-${initialDate.month.number}-${initialDate.day}"

    val currentMonthState = remember(dateKey) {
        mutableStateOf(initialDate.month.number)
    }
    val currentYearState = remember(dateKey) {
        mutableStateOf(initialDate.year)
    }

    LaunchedEffect(initialDate.year, initialDate.month.number, initialDate.day) {
        if (currentMonthState.value != initialDate.month.number || currentYearState.value != initialDate.year) {
            currentMonthState.value = initialDate.month.number
            currentYearState.value = initialDate.year
        }
    }

    return currentMonthState to currentYearState
}

@Composable
private fun createDaysList(month: Int, year: Int): List<String> {
    val daysCount = remember(month, year) {
        YearMonth(year, month).numberOfDays
    }

    return remember(daysCount) {
        (1..daysCount).map { day ->
            day.toString().padStart(2, '0')
        }
    }
}

private data class SelectionIndices(
    val dayIndex: Int,
    val monthIndex: Int,
    val yearIndex: Int
)

private fun calculateSelectionIndices(
    date: LocalDate,
    month: Int,
    year: Int,
    daysCount: Int,
    minYear: Int
): SelectionIndices {
    val dayIndex = (date.day.coerceIn(1, daysCount) - 1)
    val monthIndex = month - 1
    val yearIndex = year - minYear

    return SelectionIndices(
        dayIndex = dayIndex,
        monthIndex = monthIndex,
        yearIndex = yearIndex
    )
}

private fun convertMonthIndexToValue(monthIndex: Int): Int = monthIndex + 1

private fun convertYearIndexToValue(yearIndex: Int, minYear: Int): Int = minYear + yearIndex

private fun convertDayIndexToValue(dayIndex: Int): Int = dayIndex + 1

private fun getDaysInMonth(month: Int, year: Int): Int {
    return YearMonth(year, month).numberOfDays
}

private fun handleWheelDateChange(
    dayIndex: Int,
    monthIndex: Int,
    yearIndex: Int,
    minYear: Int,
    onMonthYearUpdate: (month: Int, year: Int) -> Unit,
    onDateChange: (day: Int, month: Int, year: Int) -> Unit
) {
    val month = convertMonthIndexToValue(monthIndex)
    val year = convertYearIndexToValue(yearIndex, minYear)

    onMonthYearUpdate(month, year)

    val daysInMonth = getDaysInMonth(month, year)
    val day = convertDayIndexToValue(dayIndex).coerceIn(1, daysInMonth)

    onDateChange(day, month, year)
}