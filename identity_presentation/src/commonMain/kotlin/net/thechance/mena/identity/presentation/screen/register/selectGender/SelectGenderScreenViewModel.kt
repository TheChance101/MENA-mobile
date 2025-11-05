package net.thechance.mena.identity.presentation.screen.register.selectGender

import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.presentation.base.BaseScreenModel

class SelectGenderScreenViewModel :
    BaseScreenModel<SelectGenderScreenUIState, SelectGenderScreenUIEffect>(
        SelectGenderScreenUIState()
    ), SelectGenderScreenInteractionListener {

    override fun onClickRegister() {
        sendNewEffect(SelectGenderScreenUIEffect.NavigateToAccountCreatedScreen)
    }

    override fun onChangeGender(gender: Gender) {
        updateState { copy(gender = gender) }
        changeIsRegisterEnabled()
    }

    private fun changeIsRegisterEnabled(){
        updateState {
            copy(isRegisterEnabled = gender != null)
        }
    }
}