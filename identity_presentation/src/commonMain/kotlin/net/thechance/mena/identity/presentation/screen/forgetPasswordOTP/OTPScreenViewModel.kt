package net.thechance.mena.identity.presentation.screen.forgetPasswordOTP

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.repository.ForgetPasswordRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage


class OTPScreenViewModel(
    private val forgetPasswordRepository: ForgetPasswordRepository,
    private val phoneNumber: String,
    private val countryCode: String
) : BaseScreenModel<OTPScreenUIState, OTPScreenUIEffect>(OTPScreenUIState()),
    OTPScreenInteractionListener {

    override val viewModelScope: CoroutineScope
        get() = screenModelScope

    init {
        startTimer()
    }

    override fun onBackClicked() {
        sendNewEffect(OTPScreenUIEffect.NavigateBack)
    }

    override fun onVerifyClicked() {
        tryToExecute(
            function = { forgetPasswordRepository.verifyOTPCode(otpCode = state.value.otpValue) },
            onSuccess = ::verifySuccess,
            onError = ::onError
        )
    }

    private fun verifySuccess() {
        sendNewEffect(OTPScreenUIEffect.NavigateToResetPassword)
    }

    override fun onOtpChanged(otp: String) {
        val filteredOtp = otp.filter { it.isDigit() }.take(OTP_LENGTH)
        if (filteredOtp == otp) {
            updateState { copy(otpValue = otp, isVerifyEnabled = filteredOtp.length == OTP_LENGTH) }
        }
    }

    override fun clearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onResendClicked() {
        tryToExecute(
            function = {
                forgetPasswordRepository.requestOTP(
                    phoneNumber = phoneNumber,
                    countryCodeName = countryCode
                )
            },
            onSuccess = ::startTimer,
            onError = ::onError
        )
    }

    private fun startTimer() {
        viewModelScope.launch {
            updateState { copy(isResendEnabled = false) }
            for (time in OTP_RESEND_TIMER_SECONDS downTo 0) {
                updateState { copy(timer = time.toString()) }
                delay(1000)
            }
            updateState { copy(isResendEnabled = true) }
        }
    }

    private fun onError(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false,
                errorMessage = mapErrorToMessage(errorState)
            )
        }
    }

    companion object {
        private const val OTP_LENGTH = 6
        private const val OTP_RESEND_TIMER_SECONDS = 60
    }
}