package net.thechance.mena.identity.presentation.feature.profileFlow.privacyAndPolicy

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface PrivacyAndPolicyScreenInteractionListener: BaseInteractionListener {

    fun onClickBack()
    fun onClearErrorMessage()
}