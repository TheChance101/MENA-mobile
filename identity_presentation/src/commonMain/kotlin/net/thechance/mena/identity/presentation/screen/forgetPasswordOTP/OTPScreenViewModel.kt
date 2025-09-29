package net.thechance.mena.identity.presentation.screen.forgetPasswordOTP

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage


class OTPScreenViewModel(
    private val resetPasswordRepository: ResetPasswordRepository,
    private val phoneNumber: String,
    private val callingCode: String,
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
            function = {
                resetPasswordRepository.verifyOTPCode(
                    otpCode = state.value.otpValue,
                    phoneNumber = PhoneNumber(
                        countryCode = callingCode,
                        localNumber = phoneNumber
                    )
                )
            },
            onSuccess = ::verifySuccess,
            onError = ::onError
        )
    }

    private fun verifySuccess() {
        sendNewEffect(
            OTPScreenUIEffect.NavigateToResetPassword(
                phoneNumber = phoneNumber,
                callingCode = callingCode,
            )
        )
    }

    override fun onOTPChanged(otp: String) {
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
                resetPasswordRepository.requestOTP(
                    phoneNumber = PhoneNumber(
                        countryCode = callingCode,
                        localNumber = phoneNumber
                    ),
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