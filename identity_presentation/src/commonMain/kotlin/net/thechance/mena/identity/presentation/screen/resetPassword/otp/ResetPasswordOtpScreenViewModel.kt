package net.thechance.mena.identity.presentation.screen.resetPassword.otp

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.jetbrains.compose.resources.StringResource

class ResetPasswordOtpScreenViewModel(
    private val resetPasswordRepository: ResetPasswordRepository,
    private val phoneNumber: String,
    private val callingCode: String,
    private val countryCode: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<ResetPasswordOtpScreenUIState, ResetPasswordOtpScreenUIEffect>(
    ResetPasswordOtpScreenUIState()
),
    ResetPasswordOtpScreenInteractionListener {

    init {
        startTimer()
    }

    override fun onClickBack() {
        sendNewEffect(ResetPasswordOtpScreenUIEffect.NavigateBack)
    }

    override fun onClickVerify() {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = { verifyOTPCode() },
            onSuccess = { onOTPVerificationSuccess() },
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
        updateState { copy(isLoading = false, otpValue = "") }
        sendNewEffect(ResetPasswordOtpScreenUIEffect.NavigateToResetPassword)
    }

    private fun onOTPVerificationError(throwable: Throwable) {
        updateState {
            copy(isLoading = false)
        }
        sendNewEffect(
            ResetPasswordOtpScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    override fun onChangeOtp(otp: String) {
        val filteredOtp = otp.filter { it.isDigit() }.take(OTP_LENGTH)
        if (filteredOtp == otp) {
            updateState { copy(otpValue = otp, isVerifyEnabled = filteredOtp.length == OTP_LENGTH) }
        }
    }

    override fun onClickResend() {
        tryToExecute(
            function = { requestNewOTP() },
            onSuccess = { onResendOTPSuccess() },
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

    private fun onResendOTPError(throwable: Throwable) {
        sendNewEffect(
            ResetPasswordOtpScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
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
                handleResetPasswordOtpException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    companion object {
        private const val OTP_LENGTH = 6
        private const val OTP_RESEND_TIMER_SECONDS = 60
    }
}