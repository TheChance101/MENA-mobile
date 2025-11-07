package net.thechance.mena.identity.presentation.screen.register.phoneEntry

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import org.jetbrains.compose.resources.StringResource

class RegisterPhoneEntryViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerRepository: RegisterRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<RegisterPhoneEntryUIState, RegisterPhoneEntryUIEffect>(
    RegisterPhoneEntryUIState()
), RegisterPhoneEntryInteractionListener {

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        tryToExecute(
            function = {
                val lastPhoneNumber = registrationDraftRepository.getLastPhoneNumber()
                lastPhoneNumber?.let { phoneNumber ->
                    registrationDraftRepository.getDraft(phoneNumber)
                }
            },
            onSuccess = { savedDraft ->
                savedDraft?.let { draft ->
                    val phoneNumber = draft.phoneNumber ?: return@let
                    updateState {
                        copy(
                            phoneNumber = phoneNumber.localNumber,
                            currentCountry = findCountryByCallingCode(phoneNumber.countryCode)
                        )
                    }
                    changeIsContinueEnabled()
                }
            },
            onError = {},
            dispatcher = dispatcher
        )
    }

    private fun findCountryByCallingCode(callingCode: String): MenaCountry {
        return MenaCountry.values().find { it.callingCode == callingCode } ?: MenaCountry.IRAQ
    }

    override fun onSelectCountryItem(country: MenaCountry) {
        updateState {
            copy(currentCountry = country, showCountryBottomSheet = false)
        }
        changeIsContinueEnabled()
    }

    override fun onDismissBottomSheet() {
        updateState {
            copy(showCountryBottomSheet = false)
        }
    }

    override fun onClickRegister() {
        updateState { copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            function = ::requestOTP,
            onSuccess = { onOTPRequestSuccess() },
            onError = ::onOTPRequestError,
            dispatcher = dispatcher
        )
    }

    private suspend fun requestOTP() {
        val phoneNumber = createPhoneNumber()
        registerRepository.requestOTP(phoneNumber, state.value.currentCountry.countryCodeName)
    }

    private fun createPhoneNumber(): PhoneNumber {
        return PhoneNumber(
            countryCode = state.value.currentCountry.callingCode,
            localNumber = state.value.phoneNumber
        )
    }

    private fun onOTPRequestSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(createNavigateToOTPEffect())
    }

    private fun createNavigateToOTPEffect(): RegisterPhoneEntryUIEffect.NavigateToOTP {
        return RegisterPhoneEntryUIEffect.NavigateToOTP(
            phoneNumber = state.value.phoneNumber,
            callingCode = state.value.currentCountry.callingCode,
            countryCode = state.value.currentCountry.countryCodeName
        )
    }

    private fun onOTPRequestError(throwable: Throwable) {
        updateState { copy(isLoading = false, errorMessage = mapErrorMessage(throwable)) }
    }

    override fun onClickCountry() {
        updateState { copy(showCountryBottomSheet = true) }
    }

    override fun onChangePhone(phone: String) {
        updateState { copy(phoneNumber = phone) }
        changeIsContinueEnabled()
        savePhoneNumber(phone)
    }

    private fun savePhoneNumber(phone: String) {
        if (phone.isNotBlank()) {
            val phoneNumber = createPhoneNumber()
            tryToExecute(
                function = {
                    val existingDraft = registrationDraftRepository.getDraft(phoneNumber)
                        ?: net.thechance.mena.identity.domain.model.RegistrationDraft()
                    registrationDraftRepository.saveDraft(
                        phoneNumber,
                        existingDraft.copy(phoneNumber = phoneNumber)
                    )
                },
                onSuccess = {},
                onError = {},
                dispatcher = dispatcher
            )
        }
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickLogin() {
        sendNewEffect(RegisterPhoneEntryUIEffect.NavigateToLogin)
    }

    private fun changeIsContinueEnabled() {
        updateState {
            val countryCode = currentCountry.callingCode
            val mobileNumberValid = loginUseCase.isMobileNumberValid(countryCode, phoneNumber)
            copy(isRegisterEnabled = mobileNumberValid)
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
}