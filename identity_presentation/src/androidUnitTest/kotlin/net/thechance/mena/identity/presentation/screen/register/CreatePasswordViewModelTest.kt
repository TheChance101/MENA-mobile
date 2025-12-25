package net.thechance.mena.identity.presentation.screen.register

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.createPassword.CreatePasswordViewModel
import net.thechance.mena.identity.presentation.screen.register.shared.RegisterUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toPhoneNumberUIState
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class CreatePasswordViewModelTest : BaseCoroutineTest() {
    private val passwordValidator = mockk<PasswordValidator>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val phoneNumber = PhoneNumber("+964", "7901234567")
    private lateinit var createPasswordViewModel: CreatePasswordViewModel

    private val validPassword = "Password123"
    private val invalidPassword = "short"

    @BeforeTest
    override fun setUp() {
        super.setUp()
        createPasswordViewModel = CreatePasswordViewModel(
            passwordValidator = passwordValidator,
            registrationDraftRepository = registrationDraftRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
        every { passwordValidator.isValid(validPassword) } returns true
        every { passwordValidator.isValid(invalidPassword) } returns false

        every { passwordValidator.isPasswordMatch(validPassword, validPassword) } returns true
        every { passwordValidator.isPasswordMatch(invalidPassword, any()) } returns false
        every { passwordValidator.isPasswordMatch(validPassword, "") } returns false
    }


    @Test
    fun `onChangeNewPassword should update state with new password`() {

        createPasswordViewModel.onChangeNewPassword(validPassword)

        assert(createPasswordViewModel.state.value.newPassword == validPassword)
    }

    @Test
    fun `onChangeConfirmPassword should update state with new password`() {

        createPasswordViewModel.onChangeNewPassword(validPassword)
        createPasswordViewModel.onChangeConfirmPassword(validPassword)

        assert(createPasswordViewModel.state.value.confirmPassword == validPassword)
    }

    @Test
    fun `onToggleNewPasswordVisibility should toggle new password visibility`() {
        createPasswordViewModel.onToggleNewPasswordVisibility()

        assert(createPasswordViewModel.state.value.isNewPasswordVisible)
    }

    @Test
    fun `onToggleConfirmPasswordVisibility should toggle confirm password visibility`() {
        createPasswordViewModel.onToggleConfirmPasswordVisibility()

        assert(createPasswordViewModel.state.value.isConfirmPasswordVisible)
    }

    @Test
    fun `loadSavedData should load saved password from draft`() = runTest {
        val savedPassword = "SavedPassword123"
        val savedDraft = RegistrationDraft(password = savedPassword)
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns savedDraft
        every { passwordValidator.isValid(savedPassword) } returns true

        val viewModel = CreatePasswordViewModel(
            passwordValidator = passwordValidator,
            registrationDraftRepository = registrationDraftRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(savedPassword, viewModel.state.value.newPassword)
    }

    @Test
    fun `onChangeNewPassword should save password when valid`() = runTest {
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns RegistrationDraft()
        coEvery { registrationDraftRepository.saveDraft(phoneNumber, any()) } returns Unit

        createPasswordViewModel.onChangeNewPassword(validPassword)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            registrationDraftRepository.saveDraft(
                phoneNumber,
                RegistrationDraft(password = validPassword)
            )
        }
    }

    @Test
    fun `onChangeNewPassword should not save password when invalid`() = runTest {

        createPasswordViewModel.onChangeNewPassword(invalidPassword)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { registrationDraftRepository.saveDraft(any(), any()) }
    }

    @Test
    fun `onChangeNewPassword should not save password when blank`() = runTest {
        every { passwordValidator.isValid("") } returns false

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { registrationDraftRepository.saveDraft(any(), any()) }
    }
}