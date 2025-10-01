package net.thechance.mena.identity.presentation.screen.forgetPasswordOTP

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OTPScreenViewModelTest {
    private val resetPasswordRepository = mockk<ResetPasswordRepository>()
    private val phoneNumber = "01100661617"
    private val countryCode = "EG"
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OTPScreenViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OTPScreenViewModel(
            resetPasswordRepository = resetPasswordRepository,
            phoneNumber = phoneNumber,
            countryCode = countryCode,
            callingCode = "002",
            dispatcher = testDispatcher
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should navigate to forget password screen when user click on back button`() = runTest {
        viewModel.effect.test {
            viewModel.onBackClicked()
            val effect = awaitItem()
            assertTrue { effect is OTPScreenUIEffect.NavigateBack }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should navigate to reset password screen when otp is correct and user click on verify button`() =
        runTest {
            val otp = "123456"
            viewModel.onOTPChanged(otp)
            coEvery { resetPasswordRepository.verifyOTPCode(otp, any()) } returns Unit

            viewModel.effect.test {
                viewModel.onVerifyClicked()
                val effect = awaitItem()
                assertTrue { effect is OTPScreenUIEffect.NavigateToResetPassword }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should show error message when otp is incorrect and user click on verify button`() =
        runTest {
            coEvery {
                resetPasswordRepository.verifyOTPCode(
                    any(),
                    any()
                )
            } throws InvalidOTPException()

            viewModel.onVerifyClicked()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue { state.errorMessage != null }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `on resend clicked should start timer and request otp again`() = runTest {
        coEvery { resetPasswordRepository.requestOTP(any(), any()) } returns Unit
        viewModel.state.test {
            viewModel.onResendClicked()
            val state = awaitItem()
            assertTrue { !state.isResendEnabled }
            cancelAndConsumeRemainingEvents()
        }
    }
}