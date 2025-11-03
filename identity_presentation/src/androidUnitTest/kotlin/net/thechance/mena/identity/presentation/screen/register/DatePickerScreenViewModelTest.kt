package net.thechance.mena.identity.presentation.screen.register

import app.cash.turbine.test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.io.discardingSink
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenUIEffect
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenViewModel
import org.junit.Before
import org.junit.Test

class DatePickerScreenViewModelTest: BaseCoroutineTest() {
    private lateinit var datePickerScreenViewModel: DatePickerScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        datePickerScreenViewModel = DatePickerScreenViewModel(
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onClickNext should navigate to select gender`() = runTest {
        datePickerScreenViewModel.effect.test {
            datePickerScreenViewModel.onClickNext()

            assert(awaitItem() == DatePickerScreenUIEffect.NavigateToSelectGender)
        }
    }

    @Test
    fun `onChangeDate should update selected date`() {
        val day = 1
        val month = 1
        val year = 2000

        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(datePickerScreenViewModel.state.value.selectedDate == LocalDate(year, month, day))
    }

    @Test
    fun `onChangeDate should enable next button if age is valid`() {
        val day = 1
        val month = 1
        val year = 2000

        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(datePickerScreenViewModel.state.value.isNextEnabled)
    }

    @Test
    fun `onChangeDate should disable next button if age is invalid`(){
        val day = 1
        val month = 1
        val year = 2025


    }
}