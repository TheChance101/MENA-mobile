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
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState
import org.jetbrains.compose.resources.StringResource

class RegisterOtpViewModel(
    private val registerRepository: RegisterRepository,
    private val registerUIState: RegisterUIState,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<RegisterOtpUIState, RegisterOtpUIEffect>(RegisterOtpUIState()),
    RegisterOtpInteractionListener {

    init {
        startTimer()
    }

    override fun onClickVerify() {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = ::verifyOTPCode,
            onSuccess = { onOTPVerificationSuccess() },
            onError = ::onOTPVerificationError,
            dispatcher = dispatcher
        )
    }

    private suspend fun verifyOTPCode() {
        registerRepository.verifyOTPCode(state.value.otpValue)
    }

    private fun onOTPVerificationSuccess() {
        updateState { copy(isLoading = false, otpValue = "") }
        sendNewEffect(createNavigateToEnterNameEffect())
    }

    private fun createNavigateToEnterNameEffect(): RegisterOtpUIEffect.NavigateToEnterName {
        return RegisterOtpUIEffect.NavigateToEnterName(
            phoneNumber = registerUIState.phoneNumber
        )
    }

    private fun onOTPVerificationError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
            )
        }
        sendNewEffect(
            RegisterOtpUIEffect.ShowSnackBarError(
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
            function = ::requestNewOTP,
            onSuccess = { onResendOTPSuccess() },
            onError = ::onResendOTPError,
            dispatcher = dispatcher
        )
    }

    private suspend fun requestNewOTP() {
        registerRepository.requestOTP(
            phoneNumber = registerUIState.phoneNumber,
            countryCodeName = registerUIState.countryCode
        )
    }

    private fun onResendOTPSuccess() {
        startTimer()
    }

    private fun onResendOTPError(throwable: Throwable) {
        sendNewEffect(
            RegisterOtpUIEffect.ShowSnackBarError(
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
                handleRegisterOtpException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    companion object {
        private const val OTP_LENGTH = 6
        private const val OTP_RESEND_TIMER_SECONDS = 60
    }
}