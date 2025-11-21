package net.thechance.mena.identity.presentation.screen.changePassword

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.helper.BaseCoroutineTest
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChangePasswordScreenViewModelTest : BaseCoroutineTest() {
    private val userRepository = mockk<UserRepository>()

    private lateinit var passwordValidator: PasswordValidator
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChangePasswordScreenViewModel

    private val validPassword = "Password123"
    private val invalidPassword = "short"

    @BeforeTest
    override fun setUp() {
        super.setUp()
        passwordValidator = mockk()

        viewModel = ChangePasswordScreenViewModel(
            userRepository = userRepository,
            passwordValidator = passwordValidator,
            dispatcher = testDispatcher
        )
        every { passwordValidator.isValid(validPassword) } returns true
        every { passwordValidator.isValid(invalidPassword) } returns false
        every { passwordValidator.isPasswordMatch(validPassword, validPassword) } returns true
        every { passwordValidator.isPasswordMatch(invalidPassword, any()) } returns false
        every { passwordValidator.isPasswordMatch(validPassword, "") } returns false


    }

    @Test
    fun `onClickBack() should set current page to 0 when current page was 1`() = runTest {
        val state = viewModel.state.value
        viewModel.onClickContinue()
        viewModel.onClickBack()
        assertTrue { state.currentPage == PasswordPage.CURRENT_PASSWORD }
    }

    @Test
    fun `onClickBack() should emit NavigateBack effect when current page is 0`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            val effect = awaitItem()
            assertTrue { effect is ChangePasswordScreenUIEffect.NavigateBack }
        }
    }

    @Test
    fun `onClickContinue() should set current page to 1 when current page is 0`() = runTest {
        viewModel.onClickContinue()
        val state = viewModel.state.value
        assertTrue { state.currentPage == PasswordPage.NEW_PASSWORD }
    }

    @Test
    fun `onClearErrorMessage() should update errorMessage to null`() = runTest {
        val state = viewModel.state.value
        viewModel.onClearErrorMessage()
        assertNull(state.errorMessage)
    }

    @Test
    fun `onToggleCurrentPasswordVisibility() should toggle isCurrentPasswordVisible`() = runTest {
        viewModel.onToggleCurrentPasswordVisibility()
        val state = viewModel.state.value.currentPasswordUIState
        assertTrue { state.isCurrentPasswordVisible }
    }

    @Test
    fun `onToggleNewPasswordVisibility() should toggle isNewPasswordVisible`() = runTest {
        viewModel.onToggleNewPasswordVisibility()
        val state = viewModel.state.value.newPasswordUIState
        assertTrue { state.isNewPasswordVisible }
    }

    @Test
    fun `onToggleConfirmPasswordVisibility() should toggle isConfirmPasswordVisible`() = runTest {
        viewModel.onToggleConfirmPasswordVisibility()
        val state = viewModel.state.value.newPasswordUIState
        assertTrue { state.isConfirmPasswordVisible }
    }

    @Test
    fun `onChangeCurrentPassword() should update currentPassword when new value is entered`() =
        runTest {
            viewModel.onChangeCurrentPassword(validPassword)
            val state = viewModel.state.value.currentPasswordUIState
            assertTrue { state.currentPassword == validPassword }
        }

    @Test
    fun `onChangeNewPassword() should update newPassword when new value is entered`() = runTest {
        viewModel.onChangeNewPassword(validPassword)
        val state = viewModel.state.value.newPasswordUIState
        assertTrue { state.newPassword == validPassword }
    }

    @Test
    fun `onChangeConfirmPassword() should update confirmPassword when new value is entered`() =
        runTest {
            viewModel.onChangeNewPassword(validPassword)
            viewModel.onChangeConfirmPassword(validPassword)
            val state = viewModel.state.value.newPasswordUIState
            assertTrue { state.confirmPassword == validPassword }
        }

    @Test
    fun `updateContinueEnabledState() should enable continue button when current password is valid`() =
        runTest {
            viewModel.onChangeCurrentPassword(validPassword)
            val state = viewModel.state.value.currentPasswordUIState
            assertTrue { state.isContinueEnabled }
        }

    @Test
    fun `updateContinueEnabledState() should disable continue button when current password is invalid`() =
        runTest {
            viewModel.onChangeCurrentPassword(invalidPassword)
            val state = viewModel.state.value.currentPasswordUIState
            assertFalse { state.isContinueEnabled }
        }

    @Test
    fun `updateSaveEnabledState() should enable save button when newPassword and confirmPassword are valid and match`() =
        runTest {
            viewModel.onChangeNewPassword(validPassword)
            viewModel.onChangeConfirmPassword(validPassword)
            val state = viewModel.state.value.newPasswordUIState
            assertTrue { state.isSaveEnabled }
        }

    @Test
    fun `updateSaveEnabledState() should disable save button when newPassword is invalid`() =
        runTest {
            viewModel.onChangeNewPassword(invalidPassword)
            val state = viewModel.state.value.newPasswordUIState
            assertFalse { state.isSaveEnabled }
        }

    @Test
    fun `updateSaveEnabledState() should disable save button when newPassword and confirmPassword are not match`() =
        runTest {
            every {
                passwordValidator.isPasswordMatch(
                    validPassword,
                    "DifferentPass123"
                )
            } returns false

            viewModel.onChangeNewPassword(validPassword)
            viewModel.onChangeConfirmPassword("DifferentPass123")
            val state = viewModel.state.value.newPasswordUIState
            assertFalse { state.isSaveEnabled }
        }

    @Test
    fun `onChangePasswordSuccess() should stop loading when changed password successfully`() =
        runTest {
            coEvery {
                userRepository.changePassword(
                    currentPassword = any(),
                    newPassword = any(),
                    confirmPassword = any()
                )
            } returns Unit

            viewModel.onClickSave()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse { state.isLoading }
        }

    @Test
    fun `onChangePasswordSuccess() should emit NavigateBack effect when changed password successfully`() =
        runTest {
            coEvery {
                userRepository.changePassword(
                    currentPassword = any(),
                    newPassword = any(),
                    confirmPassword = any()
                )
            } returns Unit


            viewModel.effect.test {
                viewModel.onClickSave()
                testDispatcher.scheduler.advanceUntilIdle()
                val effect = awaitItem()
                assertTrue { effect is ChangePasswordScreenUIEffect.NavigateBack }
            }
        }

    @Test
    fun `onChangePasswordError() should update error message when chang password throws exception`() =
        runTest {
            coEvery {
                userRepository.changePassword(
                    currentPassword = any(),
                    newPassword = any(),
                    confirmPassword = any()
                )
            } throws InvalidRequestException()

            viewModel.onClickSave()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue { state.errorMessage != null }
        }

}