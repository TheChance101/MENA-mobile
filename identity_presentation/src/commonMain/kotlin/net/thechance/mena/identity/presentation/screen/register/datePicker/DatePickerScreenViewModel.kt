package net.thechance.mena.identity.presentation.screen.register.datePicker

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DatePickerScreenViewModel :
    BaseScreenModel<DatePickerScreenUIState, DatePickerScreenUIEffect>(
        DatePickerScreenUIState()
    ), DatePickerScreenInteractionListener {

    override fun onClickNext() {
        sendNewEffect(DatePickerScreenUIEffect.NavigateToSelectGender)
    }

    override fun onChangeDate(day: Int, month: Int, year: Int) {
        tryToExecute(
            function = { updateState { copy(selectedDate = LocalDate(year, month, day)) } },
            onSuccess = { changeIsNextEnable() }
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun changeIsNextEnable() {
        val selectedDate = state.value.selectedDate
        val isAgeValid = isAgeValid(selectedDate)

        updateState { copy(isNextEnabled = isAgeValid) }
    }

    @OptIn(ExperimentalTime::class)
    private fun isAgeValid(date: LocalDate): Boolean {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val yearAdjustment = if (today.month < date.month || (today.month == date.month && today.day < date.day)) 1 else 0
        val age = today.year - date.year - yearAdjustment

        return age > MIN_AGE
    }

    companion object {
        private const val MIN_AGE = 14
    }
}