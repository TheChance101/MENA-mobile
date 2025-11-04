package net.thechance.mena.identity.presentation.screen.register.datePicker

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class DatePickerScreenUIState @OptIn(ExperimentalTime::class) constructor(
    val selectedDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val isNextEnabled: Boolean = false,
    val isNextLoading: Boolean = false
)