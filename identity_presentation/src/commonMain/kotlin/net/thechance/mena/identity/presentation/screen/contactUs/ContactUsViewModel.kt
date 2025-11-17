package net.thechance.mena.identity.presentation.screen.contactUs

import kotlinx.coroutines.CoroutineDispatcher
import net.thechance.mena.identity.domain.model.ContactInfo
import net.thechance.mena.identity.domain.repository.ApplicationInfoRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel

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
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    private fun onGetContactInfoSuccess(contactInfo: ContactInfo) {
        updateState {
            copy(
                email = contactInfo.email,
                phoneNumber = contactInfo.phoneNumber,
                facebookUrl = contactInfo.facebookAccount,
                isLoading = false
            )
        }
    }

    private fun onError(throwable: Throwable) {
        updateState { copy(isLoading = false) }
    }

    override fun onClickEmailAddress() {
        val emailUrl = "$EMAIL_URL_PREFIX${state.value.email}"
        sendNewEffect(ContactUsUIEffect.OpenUrl(emailUrl))
    }

    override fun onClickPhoneNumber() {
        val phoneUrl = "$PHONE_URL_PREFIX${state.value.phoneNumber}"
        sendNewEffect(ContactUsUIEffect.OpenUrl(phoneUrl))
    }

    override fun onClickFacebookAccount() {
        val facebookUrl = state.value.facebookUrl
        sendNewEffect(ContactUsUIEffect.OpenUrl(facebookUrl))
    }

    companion object {
        private const val EMAIL_URL_PREFIX = "mailto:"
        private const val PHONE_URL_PREFIX = "tel:"
    }
}
