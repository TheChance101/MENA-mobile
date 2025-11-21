package net.thechance.mena.identity.presentation.screen.contactUs

import kotlinx.coroutines.CoroutineDispatcher
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.model.ContactInfo
import net.thechance.mena.identity.domain.repository.ApplicationInfoRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.privacyAndPolicy.handlePrivacyAndPolicyException
import org.jetbrains.compose.resources.StringResource

class ContactUsViewModel(
    private val applicationInfoRepository: ApplicationInfoRepository,
    private val dispatcher: CoroutineDispatcher,
) :
    BaseScreenModel<ContactUsUIState, ContactUsUIEffect>(ContactUsUIState()),
    ContactUsInteractionListener {

    override fun onClickBack() = sendNewEffect(ContactUsUIEffect.NavigateBack)

    init {
        getContactInfo()
    }

    private fun getContactInfo() {
        tryToExecute(
            function = {
                applicationInfoRepository.getContactInfo()
            },
            onSuccess = ::onGetContactInfoSuccess,
            onError = ::onGetContactInfoError,
            dispatcher = dispatcher
        )
    }

    private fun onGetContactInfoSuccess(contactInfo: ContactInfo) {
        updateState {
            copy(
                email = contactInfo.email,
                phoneNumber = contactInfo.phoneNumber,
                displayedFacebookAccount = "MENA-THE-CHANCE",
                facebookUrl = contactInfo.facebookAccount,
                isLoading = false
            )
        }
    }


    override fun onClickEmailAddress() {
        if (state.value.email.isBlank())
            return

        val emailUrl = "$EMAIL_URL_PREFIX${state.value.email}"
        sendNewEffect(ContactUsUIEffect.OpenUrl(emailUrl))
    }

    override fun onClickPhoneNumber() {
        if (state.value.phoneNumber.isBlank())
            return

        val phoneUrl = "$PHONE_URL_PREFIX${state.value.phoneNumber}"
        sendNewEffect(ContactUsUIEffect.OpenUrl(phoneUrl))
    }

    override fun onClickFacebookAccount() {
        if (state.value.facebookUrl.isBlank())
            return

        sendNewEffect(ContactUsUIEffect.OpenUrl(state.value.facebookUrl))
    }

    private fun onGetContactInfoError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
            )
        }
        sendNewEffect(
            ContactUsUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handlePrivacyAndPolicyException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    companion object {
        private const val EMAIL_URL_PREFIX = "mailto:"
        private const val PHONE_URL_PREFIX = "tel:"
    }
}
