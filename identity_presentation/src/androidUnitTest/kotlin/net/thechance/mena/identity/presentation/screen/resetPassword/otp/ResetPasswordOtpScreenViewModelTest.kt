package net.thechance.mena.identity.presentation.screen.resetPassword.otp

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordOtpScreenViewModelTest {
    private val resetPasswordRepository = mockk<ResetPasswordRepository>()
    private val phoneNumber = "01100661617"
    private val countryCode = "EG"
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ResetPasswordOtpScreenViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ResetPasswordOtpScreenViewModel(
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
            viewModel.onClickBack()
            val effect = awaitItem()
            assertTrue { effect is ResetPasswordOtpScreenUIEffect.NavigateBack }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should navigate to reset password screen when otp is correct and user click on verify button`() =
        runTest {
            val otp = "123456"
            viewModel.onChangeOtp(otp)
            coEvery { resetPasswordRepository.verifyOTPCode(otp) } returns Unit

            viewModel.effect.test {
                viewModel.onClickVerify()
                val effect = awaitItem()
                assertTrue { effect is ResetPasswordOtpScreenUIEffect.NavigateToResetPassword }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should show error message when otp is incorrect and user click on verify button`() =
        runTest {
            coEvery { resetPasswordRepository.verifyOTPCode(any()) } throws Exception("Test error")

            viewModel.onClickVerify()

            viewModel.effect.test {
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isInstanceOf(ResetPasswordOtpScreenUIEffect.ShowSnackBarError::class)
            }
        }

    @Test
    fun `on resend clicked should start timer and request otp again`() = runTest {
        coEvery { resetPasswordRepository.requestOTP(any(), any()) } returns Unit
        viewModel.state.test {
            viewModel.onClickResend()
            val state = awaitItem()
            assertTrue { !state.isResendEnabled }
            cancelAndConsumeRemainingEvents()
        }
    }
}