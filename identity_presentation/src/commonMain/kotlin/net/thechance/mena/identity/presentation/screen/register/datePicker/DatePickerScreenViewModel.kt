package net.thechance.mena.identity.presentation.screen.register.datePicker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.identity.domain.useCase.validation.age.AgeValidator
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DatePickerScreenViewModel(
    private val ageValidator: AgeValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<DatePickerScreenUIState, DatePickerScreenUIEffect>(
        DatePickerScreenUIState()
    ), DatePickerScreenInteractionListener {

    override fun onClickNext() {
        sendNewEffect(DatePickerScreenUIEffect.NavigateToSelectGender)
    }

    override fun onChangeDate(day: Int, month: Int, year: Int) {
        tryToExecute(
            function = { updateState { copy(selectedDate = LocalDate(year, month, day)) } },
            onSuccess = { changeIsNextEnable() },
            dispatcher = dispatcher
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun changeIsNextEnable() {
        val selectedDate = state.value.selectedDate
        val isAgeValid = ageValidator.isValid(selectedDate)

        updateState { copy(isNextEnabled = isAgeValid) }
    }
}