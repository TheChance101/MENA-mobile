package net.thechance.mena.identity.presentation.screen.register

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.useCase.validation.age.AgeValidator
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenUIEffect
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenViewModel
import org.junit.Before
import org.junit.Test

class DatePickerScreenViewModelTest : BaseCoroutineTest() {
    private lateinit var datePickerScreenViewModel: DatePickerScreenViewModel
    private var ageValidator = mockk<AgeValidator>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        datePickerScreenViewModel = DatePickerScreenViewModel(
            ageValidator = ageValidator,
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

        every { ageValidator.isValid(LocalDate(year, month, day)) } returns true
        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(datePickerScreenViewModel.state.value.isNextEnabled)
    }

    @Test
    fun `onChangeDate should disable next button if age is invalid`() {
        val day = 1
        val month = 1
        val year = 2025

        every { ageValidator.isValid(LocalDate(year, month, day)) } returns false

        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(!datePickerScreenViewModel.state.value.isNextEnabled)
    }
}