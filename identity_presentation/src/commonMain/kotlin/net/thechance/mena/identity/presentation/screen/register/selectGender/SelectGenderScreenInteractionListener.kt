package net.thechance.mena.identity.presentation.screen.register.selectGender

import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface SelectGenderScreenInteractionListener : BaseInteractionListener {
    fun onClickRegister()
    fun onChangeGender(gender: Gender)
}