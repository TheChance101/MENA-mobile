package net.thechance.mena.identity.presentation.screen.forgetPassword

import app.cash.turbine.test
import dev.mokkery.mock
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.MobileNumberValidator
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ForgetPasswordScreenViewModelTest {
    private val resetPasswordRepository = mockk<ResetPasswordRepository>()
    private lateinit var useCase: LoginUseCase
    private val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: ForgetPasswordScreenViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = LoginUseCase(
            authenticationRepository = mock<AuthenticationRepository>(),
            mobileNumberValidator = MobileNumberValidator()
        )
        viewModel = ForgetPasswordScreenViewModel(
            loginUseCase = useCase,
            resetPasswordRepository = resetPasswordRepository,
            dispatcher = testDispatcher
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should navigate to login screen when user click on back button`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            val effect = awaitItem()
            assertTrue { effect is ForgetPasswordScreenUIEffect.NavigateBack }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `on continue button clicked should navigate to otp screen when user enter valid phone number`() =
        runTest {
            val phoneNumber = "01100661617"
            viewModel.onChangePhone(phoneNumber)
            viewModel.onSelectCountryItem(MenaCountry.EGYPT)
            coEvery {
                resetPasswordRepository.requestOTP(
                    any(),
                    any()
                )
            } returns Unit

            viewModel.effect.test {
                viewModel.onClickContinue()

                val effect = awaitItem()
                assertTrue { effect is ForgetPasswordScreenUIEffect.NavigateToOTP }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `on continue button clicked should show error message when user enter invalid phone number`() =
        runTest {
            val phoneNumber = "01100661617"
            coEvery {
                resetPasswordRepository.requestOTP(any(), any())
            } throws InvalidMobileNumberException("")
            viewModel.onChangePhone(phoneNumber)
            viewModel.onSelectCountryItem(MenaCountry.EGYPT)
            viewModel.onDismissBottomSheet()

            viewModel.onClickContinue()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue { state.errorMessage != null }
                cancelAndConsumeRemainingEvents()
            }
        }
}