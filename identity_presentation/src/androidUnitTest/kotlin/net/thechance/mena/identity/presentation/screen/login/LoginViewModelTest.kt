package net.thechance.mena.identity.presentation.screen.login

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)

class LoginViewModelTest {
    private lateinit var useCase: LoginUseCase
    private lateinit var viewModel: LoginScreenViewModel
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk(relaxed = true)
        viewModel = LoginScreenViewModel(useCase, testDispatcher)

    }


    private fun setupValidCountry() {
        viewModel.onConfirmCountryItem(selectedCountry)
    }

    @Test
    fun `should navigate to home screen when login success`() = runTest {

        coEvery { useCase.login(any(), any(), any()) } returns Unit

        viewModel.effect.test {
            viewModel.onLoginClicked()

            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue { effect is LoginScreenUIEffect.NavigateToHome }
            cancelAndConsumeRemainingEvents()
        }

    }

    @Test
    fun `should show snack bar error when mobile number is wrong`() = runTest {
        coEvery { useCase.login(any(), any(), any()) } throws Exception("Test error")

        viewModel.onLoginClicked()

        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(LoginScreenUIEffect.ShowSnackBarError::class)
        }
    }


    @Test
    fun `should change phone number when user type on phone number text field`() =
        runTest {

            val phoneNumber = "1100661617"

            viewModel.onPhoneChanged(phoneNumber)

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { viewModel.state.value.phoneNumber == phoneNumber }

        }

    @Test
    fun `should change password when user type on password text field`() = runTest {

        val password = "12345678"

        viewModel.onPasswordChanged(password)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue { viewModel.state.value.password == password }

    }

    @Test
    fun `should change country code when user select country code from country code dialog`() =
        runTest {

            setupValidCountry()

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { viewModel.state.value.currentCountry == selectedCountry }

        }

    @Test
    fun `should login button is enabled when phone number and password are valid`() =
        runTest {

            val phoneNumber = "01100661617"
            val password = "12345678"
            every { useCase.isMobileNumberValid(any(), any()) } returns true
            every { useCase.isPasswordValid(any()) } returns true

            setupValidCountry()
            viewModel.onPhoneChanged(phoneNumber)
            viewModel.onPasswordChanged(password)

            testDispatcher.scheduler.advanceUntilIdle()

            val isLoginEnabled = viewModel.state.value.isLoginEnabled
            assertTrue { isLoginEnabled }

        }

    @Test
    fun `should login button is disabled when phone number is empty`() = runTest {
        val password = "12345678"

        viewModel.onPasswordChanged(password)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue { !viewModel.state.value.isLoginEnabled }

    }

    @Test
    fun `should login button is disabled when password is empty`() = runTest {
        val phoneNumber = "1100661617"

        viewModel.onConfirmCountryItem(selectedCountry)
        viewModel.onPhoneChanged(phoneNumber)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue { !viewModel.state.value.isLoginEnabled }

    }

    @Test
    fun `should login button is disabled when password length is less than 8 characters`() =
        runTest {
            val phoneNumber = "1100661617"
            val password = "1234"

            setupValidCountry()
            viewModel.onPhoneChanged(phoneNumber)
            viewModel.onPasswordChanged(password)

            testDispatcher.scheduler.advanceUntilIdle()


            assertTrue { !viewModel.state.value.isLoginEnabled }

        }

    @Test
    fun `should login button is disabled when phone number does not match  country code`() =
        runTest {
            val phoneNumber = "11006616"
            val password = "12345678"

            setupValidCountry()
            viewModel.onPhoneChanged(phoneNumber)
            viewModel.onPasswordChanged(password)

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { !viewModel.state.value.isLoginEnabled }

        }

    @Test
    fun `should change password visibility when user click on password visibility button`() =
        runTest {

            viewModel.onPasswordVisibilityToggled()

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { viewModel.state.value.isPasswordVisible }

        }

    @Test
    fun `should navigate to forget password screen when user click on forget password button`() =
        runTest {

            viewModel.effect.test {
                viewModel.onForgotPasswordClicked()
                val effect = awaitItem()
                assertTrue { effect is LoginScreenUIEffect.NavigateToForgotPassword }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should navigate to register screen when user click on register button`() =
        runTest {

            viewModel.effect.test {
                viewModel.onRegisterClicked()
                val effect = awaitItem()
                assertTrue { effect is LoginScreenUIEffect.NavigateToRegister }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should show country bottom sheet when user click on phone code`() =
        runTest {
            viewModel.onPhoneCodeClicked()

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { viewModel.state.value.showCountryBottomSheet }

        }

    @Test
    fun `should dismiss country bottom sheet when user close country bottom sheet`() =
        runTest {
            viewModel.onDismissBottomSheet()

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { !viewModel.state.value.showCountryBottomSheet }

        }

    companion object {
        val selectedCountry: MenaCountry = MenaCountry.EGYPT
    }

}