package net.thechance.mena.identity.presentation.feature.profileFlow.contactUs

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface ContactUsInteractionListener : BaseInteractionListener {
    fun onClickBack()
    fun onClickEmailAddress()
    fun onClickPhoneNumber()
    fun onClickFacebookAccount()
}