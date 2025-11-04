package net.thechance.mena.identity.presentation.screen.register.otp

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.jetbrains.compose.resources.StringResource

class RegisterOtpViewModel(
    private val registerRepository: RegisterRepository,
    private val phoneNumber: String,
    private val callingCode: String,
    private val countryCode: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<RegisterOtpUIState, RegisterOtpUIEffect>(RegisterOtpUIState()),
    RegisterOtpInteractionListener {

    init {
        startTimer()
    }

    override fun onClickVerify() {
        tryToExecute(
            function = ::verifyOTPCode,
            onSuccess = { onOTPVerificationSuccess() },
            onError = ::onOTPVerificationError,
            dispatcher = dispatcher
        )
    }

    private suspend fun verifyOTPCode() {
        // TODO: Uncomment when ready to integrate with backend
        // registerRepository.verifyOTPCode(
        //     otpCode = state.value.otpValue,
        // )

        // Bypass for UI testing - just validate OTP length
        delay(1000) // Simulate network delay
        if (state.value.otpValue.length != OTP_LENGTH) {
            throw Exception("Invalid OTP")
        }
    }

    private fun onOTPVerificationSuccess() {
        sendNewEffect(RegisterOtpUIEffect.NavigateToCreatePassword)
        updateState { copy(otpValue = "") }
    }

    private fun onOTPVerificationError(throwable: Throwable) {
        updateState { copy(errorMessage = mapErrorMessage(throwable)) }
    }

    override fun onChangeOtp(otp: String) {
        val filteredOtp = otp.filter { it.isDigit() }.take(OTP_LENGTH)
        if (filteredOtp == otp) {
            updateState { copy(otpValue = otp, isVerifyEnabled = filteredOtp.length == OTP_LENGTH) }
        }
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickResend() {
        tryToExecute(
            function = ::requestNewOTP,
            onSuccess = { onResendOTPSuccess() },
            onError = ::onResendOTPError,
            dispatcher = dispatcher
        )
    }

    private suspend fun requestNewOTP() {
        // TODO: Uncomment when ready to integrate with backend
        // registerRepository.requestOTP(
        //     phoneNumber = PhoneNumber(
        //         countryCode = callingCode,
        //         localNumber = phoneNumber
        //     ),
        //     countryCodeName = countryCode
        // )

        // Bypass for UI testing
        delay(500) // Simulate network delay
    }

    private fun onResendOTPSuccess() {
        startTimer()
    }

    private fun onResendOTPError(throwable: Throwable) {
        updateState { copy(errorMessage = mapErrorMessage(throwable)) }
    }

    private fun startTimer() {
        screenModelScope.launch {
            updateState { copy(isResendEnabled = false) }
            for (time in OTP_RESEND_TIMER_SECONDS downTo 0) {
                updateState { copy(timer = time.toString()) }
                delay(1000)
            }
            updateState { copy(isResendEnabled = true) }
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleAuthenticationException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    companion object {
        private const val OTP_LENGTH = 6
        private const val OTP_RESEND_TIMER_SECONDS = 60
    }
}