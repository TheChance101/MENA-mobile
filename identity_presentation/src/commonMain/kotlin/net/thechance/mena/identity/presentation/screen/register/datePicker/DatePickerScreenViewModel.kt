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
            function = {updateState { copy(selectedDate = LocalDate(year, month, day)) }},
            onSuccess = { checkEnableNext() }
        )
    }


    @OptIn(ExperimentalTime::class)
    private fun checkEnableNext() {
        val selectedDate = state.value.selectedDate
        val isAgeValid = selectedDate.let { date ->
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val age = today.year - date.year -
                    if (today.month < date.month ||
                        (today.month == date.month && today.day < date.day)
                    ) 1 else 0
            age > 14
        }

        updateState {
            copy(isNextEnabled = isAgeValid)
        }
    }
}