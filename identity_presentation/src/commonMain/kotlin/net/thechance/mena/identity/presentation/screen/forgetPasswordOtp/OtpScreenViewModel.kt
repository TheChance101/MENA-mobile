package net.thechance.mena.identity.presentation.screen.forgetPasswordOtp

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage

class OtpScreenViewModel(
    private val resetPasswordRepository: ResetPasswordRepository,
    private val phoneNumber: String,
    private val callingCode: String,
    private val countryCode: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<OtpScreenUIState, OtpScreenUIEffect>(OtpScreenUIState()),
    OtpScreenInteractionListener {

    init {
        startTimer()
    }

    override fun onClickBack() {
        sendNewEffect(OtpScreenUIEffect.NavigateBack)
    }

    override fun onClickVerify() {
        tryToExecute(
            function = ::verifyOTPCode,
            onSuccess = ::onOTPVerificationSuccess,
            onError = ::onOTPVerificationError,
            dispatcher = dispatcher
        )
    }

    private suspend fun verifyOTPCode() {
        resetPasswordRepository.verifyOTPCode(
            otpCode = state.value.otpValue,
        )
    }

    private fun onOTPVerificationSuccess() {
        sendNewEffect(OtpScreenUIEffect.NavigateToResetPassword)
        updateState { copy(otpValue = "") }
    }

    private fun onOTPVerificationError(errorState: ErrorState) {
        updateState { copy(errorMessage = mapErrorToMessage(errorState)) }
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
            onSuccess = ::onResendOTPSuccess,
            onError = ::onResendOTPError,
            dispatcher = dispatcher
        )
    }

    private suspend fun requestNewOTP() {
        resetPasswordRepository.requestOTP(
            phoneNumber = PhoneNumber(
                countryCode = callingCode,
                localNumber = phoneNumber
            ),
            countryCodeName = countryCode
        )
    }

    private fun onResendOTPSuccess() {
        startTimer()
    }

    private fun onResendOTPError(errorState: ErrorState) {
        updateState { copy(errorMessage = mapErrorToMessage(errorState)) }
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

    companion object {
        private const val OTP_LENGTH = 6
        private const val OTP_RESEND_TIMER_SECONDS = 60
    }
}