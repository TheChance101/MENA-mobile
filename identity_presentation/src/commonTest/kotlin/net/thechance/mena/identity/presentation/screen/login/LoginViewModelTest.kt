package net.thechance.mena.identity.presentation.screen.login

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)

class LoginViewModelTest {

    private lateinit var useCase: LoginUseCase
    private lateinit var viewModel: LoginScreenModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = mock(mode = MockMode.autofill)
        viewModel = LoginScreenModel(useCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupValidCountry() {
        viewModel.onSelectCountryItem(selectedCountry)
        viewModel.onClickConfirmButton()
    }

    @Test
    fun `should navigate to home screen when login success`() = runTest {

        everySuspend { useCase.login(any(), any(), any()) } returns Unit


        viewModel.effect.test {
            viewModel.onLoginClicked()

            val effect = awaitItem()
            assertTrue { effect is LoginScreenUIEffect.NavigateToHome }
            cancelAndConsumeRemainingEvents()
        }

    }

    @Test
    fun `should show invalid mobile number message when mobile number is wrong`() = runTest {

        val errorMessage = "Invalid mobile number"
        everySuspend {
            useCase.login(
                any(),
                any(),
                any()
            )
        } throws InvalidMobileNumberException("11006600171")

        viewModel.onLoginClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertIs<String>(state.errorMessage, errorMessage)
            cancelAndConsumeRemainingEvents()
        }

    }

    @Test
    fun `should change phone number when user type on phone number text field`() = runTest {

        val phoneNumber = "1100661617"

        viewModel.onPhoneChanged(phoneNumber)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue { state.phoneNumber == phoneNumber }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should change password when user type on password text field`() = runTest {

        val password = "12345678"

        viewModel.onPasswordChanged(password)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue { state.password == password }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should change country code when user select country code from country code dialog`() =
        runTest {

            setupValidCountry()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue { state.countryPickerUIState.currentCountry == selectedCountry }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should login button is enabled when phone number and password are valid`() = runTest {

        val phoneNumber = "01100661617"
        val password = "12345678"
        every { useCase.isMobileNumberValid(any(), any()) } returns true
        every { useCase.isPasswordValid(any()) } returns true

        setupValidCountry()
        viewModel.onPhoneChanged(phoneNumber)
        viewModel.onPasswordChanged(password)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue { state.isLoginEnabled }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should login button is disabled when phone number is empty`() = runTest {
        val password = "12345678"

        viewModel.onPasswordChanged(password)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue { !state.isLoginEnabled }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should login button is disabled when password is empty`() = runTest {
        val phoneNumber = "1100661617"

        viewModel.onSelectCountryItem(selectedCountry)
        viewModel.onClickConfirmButton()
        viewModel.onPhoneChanged(phoneNumber)

        viewModel.state.test {

            val state = awaitItem()
            assertTrue { !state.isLoginEnabled }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should login button is disabled when password length is less than 8 characters`() =
        runTest {
            val phoneNumber = "1100661617"
            val password = "1234"

            setupValidCountry()
            viewModel.onPhoneChanged(phoneNumber)
            viewModel.onPasswordChanged(password)

            viewModel.state.test {
                val state = awaitItem()
                assertTrue { !state.isLoginEnabled }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should login button is disabled when phone number does not match  country code`() =
        runTest {
            val phoneNumber = "11006616"
            val password = "12345678"

            setupValidCountry()
            viewModel.onPhoneChanged(phoneNumber)
            viewModel.onPasswordChanged(password)

            viewModel.state.test {
                val state = awaitItem()
                assertTrue { !state.isLoginEnabled }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should change password visibility when user click on password visibility button`() =
        runTest {

            viewModel.onPasswordVisibilityToggled()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue { state.isPasswordVisible }
                cancelAndConsumeRemainingEvents()
            }

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
    fun `should navigate to register screen when user click on register button`() = runTest {
        viewModel.effect.test {
            viewModel.onRegisterClicked()
            val effect = awaitItem()
            assertTrue { effect is LoginScreenUIEffect.NavigateToRegister }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should show country bottom sheet when user click on phone code`() = runTest {
        viewModel.onPhoneCodeClicked()
        viewModel.state.test {
            val state = awaitItem()
            assertTrue { state.showCountryBottomSheet }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should dismiss country bottom sheet when user close country bottom sheet`() = runTest {
        viewModel.onDismissBottomSheet()
        viewModel.state.test {
            val state = awaitItem()
            assertTrue { !state.showCountryBottomSheet }
            cancelAndConsumeRemainingEvents()
        }
    }


    companion object {
        val selectedCountry: MenaCountry = MenaCountry.EGYPT
    }

}