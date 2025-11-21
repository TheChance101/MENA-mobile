package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.exception.InvalidPasswordException
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SetNewPasswordScreenViewModelTest {

    private lateinit var resetPasswordRepository: ResetPasswordRepository
    private lateinit var passwordValidator: PasswordValidator
    private lateinit var viewModel: SetNewPasswordScreenViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val validPassword = "Password123"
    private val invalidPassword = "short"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        resetPasswordRepository = mockk()
        passwordValidator = mockk()

        viewModel = SetNewPasswordScreenViewModel(
            passwordValidator = passwordValidator,
            resetPasswordRepository = resetPasswordRepository,
            dispatcher = testDispatcher
        )

        every { passwordValidator.isValid(validPassword) } returns true
        every { passwordValidator.isValid(invalidPassword) } returns false
        every { passwordValidator.isPasswordMatch(validPassword, validPassword) } returns true
        every { passwordValidator.isPasswordMatch(invalidPassword, any()) } returns false
        every { passwordValidator.isPasswordMatch(validPassword, "") } returns false


    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupValidPasswords() {
        viewModel.onChangeNewPassword(validPassword)
        viewModel.onChangeConfirmPassword(validPassword)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun `onChangeNewPassword should update newPassword in state`() = runTest {
        val newPass = "NewP@ss123"
        every { passwordValidator.isValid(newPass) } returns true
        every { passwordValidator.isPasswordMatch(any(), any()) } returns true

        viewModel.onChangeNewPassword(newPass)

        viewModel.state.test {
            assertTrue(awaitItem().newPassword == newPass)
        }
    }

    @Test
    fun `onToggleNewPasswordVisibility should toggle isNewPasswordVisible state`() = runTest {
        viewModel.onToggleNewPasswordVisibility()

        viewModel.state.test {
            assertTrue(awaitItem().isNewPasswordVisible)
        }
    }

    @Test
    fun `onToggleConfirmPasswordVisibility should toggle isConfirmPasswordVisible state`() =
        runTest {
            viewModel.onToggleConfirmPasswordVisibility()

            viewModel.state.test {
                assertTrue(awaitItem().isConfirmPasswordVisible)
            }
        }

    @Test
    fun `checkResetButtonEnabled should be disabled when passwords do not match`() = runTest {
        every { passwordValidator.isPasswordMatch(validPassword, "DifferentPass123") } returns false

        viewModel.onChangeNewPassword(validPassword)
        viewModel.onChangeConfirmPassword("DifferentPass123")


        viewModel.state.test {
            assertFalse(awaitItem().isResetEnabled)
        }
    }

    @Test
    fun `checkResetButtonEnabled should be disabled when password is not secure`() = runTest {
        viewModel.onChangeNewPassword(invalidPassword)
        viewModel.onChangeConfirmPassword(invalidPassword)

        viewModel.state.test {
            assertFalse(awaitItem().isResetEnabled)
        }
    }

    @Test
    fun `checkResetButtonEnabled should be enabled when both passwords match and are secure`() =
        runTest {
            setupValidPasswords()

            viewModel.state.test {
                assertTrue(awaitItem().isResetEnabled)
            }
        }

    @Test
    fun `onClickResetPassword should show dialog when passwords match and are secure`() = runTest {
        setupValidPasswords()
        coEvery { resetPasswordRepository.resetPassword(any(), any()) } returns Unit

        viewModel.onClickResetPassword()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertTrue(awaitItem().isDialogVisible)
        }

    }

    @Test
    fun `onClickResetPassword should show error message when passwords do not match`() = runTest {
        every { passwordValidator.isPasswordMatch(validPassword, "DifferentPass123") } returns false

        viewModel.onChangeNewPassword(validPassword)
        viewModel.onChangeConfirmPassword("DifferentPass123")

        viewModel.onClickResetPassword()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertTrue(awaitItem().errorMessage != null)
        }
    }

    @Test
    fun `onClickOk should send NavigateBackToLogin effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickOk()
            assertTrue(awaitItem() is SetNewPasswordScreenUIEffect.NavigateBackToLogin)
        }
    }

    @Test
    fun `onClearErrorMessage should clear errorMessage in state when reset password throw exception`() =
        runTest {
            coEvery {
                resetPasswordRepository.resetPassword(
                    any(),
                    any()
                )
            } throws InvalidPasswordException()

            viewModel.onClickResetPassword()
            testDispatcher.scheduler.advanceUntilIdle()


            viewModel.onClearErrorMessage()
            viewModel.state.test {
                assertTrue(awaitItem().errorMessage == null)
            }
        }

    @Test
    fun `onClickBack should send NavigateBackToLogin effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            assertTrue(awaitItem() is SetNewPasswordScreenUIEffect.NavigateBackToLogin)
        }
    }
}
