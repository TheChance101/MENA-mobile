package net.thechance.mena.identity.presentation.screen.forgetPassword

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.throws
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
import net.thechance.mena.identity.domain.repository.ForgetPasswordRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ForgetPasswordScreenViewModelTest {
    private val forgetPasswordRepository = mock<ForgetPasswordRepository>()
    private val useCase: LoginUseCase  = mock(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: ForgetPasswordScreenViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase
        viewModel = ForgetPasswordScreenViewModel(
            loginUseCase = useCase,
            forgetPasswordRepository = forgetPasswordRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should navigate to login screen when user click on back button`() = runTest {
        viewModel.effect.test {
            viewModel.onBackClicked()
            val effect = awaitItem()
            assertTrue { effect is ForgetPasswordScreenUIEffect.NavigateBack }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `on continue button clicked should navigate to otp screen when user enter valid phone number`() =
        runTest {
            val phoneNumber = "01100661617"
            viewModel.onPhoneChanged(phoneNumber)
            viewModel.onSelectCountryItem(MenaCountry.EGYPT)
            viewModel.onClickConfirmButton()

            viewModel.effect.test {
                viewModel.onContinueClicked()

                val effect = awaitItem()
                assertTrue { effect is ForgetPasswordScreenUIEffect.NavigateToOTP }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `on continue button clicked should show error message when user enter invalid phone number`() =
        runTest {
            val phoneNumber = "0110066161"
            everySuspend {
                forgetPasswordRepository.requestOTP(
                    phoneNumber,
                    any()
                )
            } throws InvalidMobileNumberException("11006600171")

            viewModel.onContinueClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue { state.errorMessage != null }
                cancelAndConsumeRemainingEvents()
            }
        }
}