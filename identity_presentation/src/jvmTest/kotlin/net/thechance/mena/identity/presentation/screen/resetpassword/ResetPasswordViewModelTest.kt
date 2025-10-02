package net.thechance.mena.identity.presentation.screen.resetpassword

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
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.presentation.screen.resetPassword.ResetPasswordScreenUIEffect
import net.thechance.mena.identity.presentation.screen.resetPassword.ResetPasswordScreenViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordScreenViewModelTest {

    private lateinit var resetPasswordRepository: ResetPasswordRepository
    private lateinit var passwordValidator: PasswordValidator
    private lateinit var viewModel: ResetPasswordScreenViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val validPassword = "Password123"
    private val invalidPassword = "short"
    private val mockkPhoneNumber = "01123456789"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        resetPasswordRepository = mockk()
        passwordValidator = mockk()

        viewModel = ResetPasswordScreenViewModel(
            passwordValidator = passwordValidator,
            resetPasswordRepository = resetPasswordRepository,
            phoneNumber = mockkPhoneNumber,
            callingCode = mockkPhoneNumber,
            dispatcher = testDispatcher
        )

        every { passwordValidator.isValid(validPassword) } returns true
        every { passwordValidator.isValid(invalidPassword) } returns false
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
    fun `onToggleConfirmPasswordVisibility should toggle isConfirmPasswordVisible state`() = runTest {
        viewModel.onToggleConfirmPasswordVisibility()

        viewModel.state.test {
            assertTrue(awaitItem().isConfirmPasswordVisible)
        }
    }

    @Test
    fun `checkResetButtonEnabled should be disabled when passwords do not match`() = runTest {
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
    fun `checkResetButtonEnabled should be enabled when both passwords match and are secure`() = runTest {
        setupValidPasswords()

        viewModel.state.test {
            assertTrue(awaitItem().isResetEnabled)
        }
    }

    @Test
    fun `onClickResetPassword should call Repository and navigate on success`() = runTest {
        setupValidPasswords()

        coEvery {
            resetPasswordRepository.resetPassword(
                validPassword,
                validPassword,
                PhoneNumber(countryCode = mockkPhoneNumber, localNumber = mockkPhoneNumber)
            )
        } returns Unit

        viewModel.effect.test {
            viewModel.onClickResetPassword()

            viewModel.state.test {
                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)

                val finishedState = awaitItem()
                assertFalse(finishedState.isLoading)
            }

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(awaitItem() is ResetPasswordScreenUIEffect.NavigateBackToLogin)
        }
    }

    @Test
    fun `onClickBack should send NavigateBackToLogin effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            assertTrue(awaitItem() is ResetPasswordScreenUIEffect.NavigateBackToLogin)
        }
    }
}
