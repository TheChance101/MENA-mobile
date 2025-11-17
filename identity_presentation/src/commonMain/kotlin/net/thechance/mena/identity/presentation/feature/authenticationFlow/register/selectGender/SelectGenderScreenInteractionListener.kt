package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.selectGender

import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface SelectGenderScreenInteractionListener : BaseInteractionListener {
    fun onClickRegister()
    fun onChangeGender(gender: Gender)
    fun onClearErrorMessage()
}