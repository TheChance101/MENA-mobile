package net.thechance.mena.identity.presentation.screen.profile.contactUs

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ContactUsInteractionListener : BaseInteractionListener {
    fun onClickBack()
    fun onClickEmailAddress()
    fun onClickPhoneNumber()
    fun onClickFacebookAccount()
}