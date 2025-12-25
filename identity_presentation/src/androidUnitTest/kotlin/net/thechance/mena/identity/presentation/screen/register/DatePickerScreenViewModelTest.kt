package net.thechance.mena.identity.presentation.screen.register

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.useCase.validation.age.AgeValidator
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenUIEffect
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.shared.RegisterUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toPhoneNumberUIState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatePickerScreenViewModelTest : BaseCoroutineTest() {
    private lateinit var datePickerScreenViewModel: DatePickerScreenViewModel
    private var ageValidator = mockk<AgeValidator>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val phoneNumber = net.thechance.mena.identity.domain.entity.PhoneNumber("+964", "7901234567")

    @Before
    fun setup() {
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns null
        datePickerScreenViewModel = DatePickerScreenViewModel(
            ageValidator = ageValidator,
            registrationDraftRepository = registrationDraftRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onClickNext should navigate to select gender`() = runTest {
        datePickerScreenViewModel.effect.test {
            // Set a valid date first
            every { ageValidator.isValid(LocalDate(2000, 1, 1)) } returns true
            testDispatcher.scheduler.advanceUntilIdle()
            datePickerScreenViewModel.onChangeDate(1, 1, 2000)
            testDispatcher.scheduler.advanceUntilIdle()
            
            datePickerScreenViewModel.onClickNext()

            val effect = awaitItem()
            assert(effect is DatePickerScreenUIEffect.NavigateToSelectGender)
        }
    }

    @Test
    fun `onChangeDate should update selected date`() = runTest {
        val day = 1
        val month = 1
        val year = 2000

        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns null
        every { ageValidator.isValid(LocalDate(year, month, day)) } returns true
        testDispatcher.scheduler.advanceUntilIdle()
        
        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(datePickerScreenViewModel.state.value.selectedDate == LocalDate(year, month, day))
    }

    @Test
    fun `onChangeDate should enable next button if age is valid`() = runTest {
        val day = 1
        val month = 1
        val year = 2000

        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns null
        every { ageValidator.isValid(LocalDate(year, month, day)) } returns true
        testDispatcher.scheduler.advanceUntilIdle()
        
        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(datePickerScreenViewModel.state.value.isNextEnabled)
    }

    @Test
    fun `onChangeDate should disable next button if age is invalid`() = runTest {
        val day = 1
        val month = 1
        val year = 2025

        every { ageValidator.isValid(LocalDate(year, month, day)) } returns false

        testDispatcher.scheduler.advanceUntilIdle()
        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(!datePickerScreenViewModel.state.value.isNextEnabled)
    }

    @Test
    fun `loadSavedData should load saved birth date from draft`() = runTest {
        val savedDate = LocalDate(2000, 1, 1)
        val savedDraft = RegistrationDraft(birthDate = savedDate)
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns savedDraft
        every { ageValidator.isValid(savedDate) } returns true

        val viewModel = DatePickerScreenViewModel(
            ageValidator = ageValidator,
            registrationDraftRepository = registrationDraftRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(savedDate, viewModel.state.value.selectedDate)
    }

    @Test
    fun `loadSavedData should enable next button when saved date is valid`() = runTest {
        val savedDate = LocalDate(2000, 1, 1)
        val savedDraft = RegistrationDraft(birthDate = savedDate)
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns savedDraft
        every { ageValidator.isValid(savedDate) } returns true

        val viewModel = DatePickerScreenViewModel(
            ageValidator = ageValidator,
            registrationDraftRepository = registrationDraftRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.isNextEnabled)
    }

    @Test
    fun `onChangeDate should save birth date when valid`() = runTest {
        val day = 1
        val month = 1
        val year = 2000
        val date = LocalDate(year, month, day)
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns null
        every { ageValidator.isValid(date) } returns true
        coEvery { registrationDraftRepository.saveDraft(phoneNumber, any()) } returns Unit

        testDispatcher.scheduler.advanceUntilIdle()
        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { registrationDraftRepository.saveDraft(phoneNumber, RegistrationDraft(birthDate = date)) }
    }

    @Test
    fun `onChangeDate should not save birth date when invalid`() = runTest {
        val day = 1
        val month = 1
        val year = 2025
        val date = LocalDate(year, month, day)
        every { ageValidator.isValid(date) } returns false

        datePickerScreenViewModel.onChangeDate(day, month, year)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { registrationDraftRepository.saveDraft(any(), any()) }
    }
}