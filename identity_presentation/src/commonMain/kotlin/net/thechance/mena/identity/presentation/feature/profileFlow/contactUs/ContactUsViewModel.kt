package net.thechance.mena.identity.presentation.feature.profileFlow.contactUs

import net.thechance.mena.identity.presentation.core.base.BaseScreenModel

class ContactUsViewModel :
    BaseScreenModel<ContactUsUIState, ContactUsUIEffect>(ContactUsUIState()),
    ContactUsInteractionListener {

    override fun onClickBack() = sendNewEffect(ContactUsUIEffect.NavigateBack)

    override fun onClickEmailAddress() {
        val emailUrl = "mailto:${state.value.email}"
        sendNewEffect(ContactUsUIEffect.OpenUrl(emailUrl))
    }

    override fun onClickPhoneNumber() {
        val phoneUrl = "tel:${state.value.phoneNumber}"
        sendNewEffect(ContactUsUIEffect.OpenUrl(phoneUrl))
    }

    override fun onClickFacebookAccount() {
        val facebookUrl = state.value.facebookUrl
        sendNewEffect(ContactUsUIEffect.OpenUrl(facebookUrl))
    }
}