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
import net.thechance.mena.identity.domain.entity.PhoneNumber as PhoneNumberEntity

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
        updateState { copy(isLoading = true, errorMessage = null) }
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
            phoneNumber = createPhoneNumber()
        )
    }

    private fun createPhoneNumber(): PhoneNumberEntity {
        return PhoneNumberEntity(
            countryCode = callingCode,
            localNumber = phoneNumber
        )
    }

    private fun onOTPVerificationError(throwable: Throwable) {
        updateState { 
            copy(
                isLoading = false,
                errorMessage = mapErrorMessage(throwable)
            ) 
        }
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
        registerRepository.requestOTP(
            phoneNumber = createPhoneNumber(),
            countryCodeName = countryCode
        )
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