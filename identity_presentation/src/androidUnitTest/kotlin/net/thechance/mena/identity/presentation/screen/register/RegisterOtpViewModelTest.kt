package net.thechance.mena.identity.presentation.screen.register

import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.otp.RegisterOtpViewModel
import org.junit.Before
import org.junit.Test

class RegisterOtpViewModelTest : BaseCoroutineTest() {
    private lateinit var registerOtpViewModel: RegisterOtpViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val registerRepository = mockk<RegisterRepository>()
    private val phoneNumber = "123456789"
    private val callingCode = "+1"
    private val countryCode = "US"


    @Before
    fun setup() {
        registerOtpViewModel = RegisterOtpViewModel(
            registerRepository = mockk(),
            phoneNumber = phoneNumber,
            callingCode = callingCode,
            countryCode = countryCode,
            dispatcher = testDispatcher
        )
    }


    @Test
    fun `onChangeOtp should update otp value and enable verify button if otp length is 6`() {
        val otp = "123456"

        registerOtpViewModel.onChangeOtp(otp)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(registerOtpViewModel.state.value.otpValue == otp)
        assert(registerOtpViewModel.state.value.isVerifyEnabled)
    }
}