package net.thechance.mena.identity.presentation.screen.register.enterName

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.presentation.base.BaseScreenModel

class EnterNameViewModel(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<EnterNameUIState, EnterNameUIEffect>(EnterNameUIState()),
    EnterNameInteractionListener {

    override fun onChangeFirstName(name: String) {
        updateState {
            val newState = this.copy(firstName = name)
            copy(
                firstName = name,
                isNextEnabled = newState.isValidInput()
            )
        }
    }

    override fun onLastNameChange(name: String) {
        updateState {
            val newState = this.copy(lastName = name)
            copy(
                lastName = name,
                isNextEnabled = newState.isValidInput()
            )
        }
    }

    override fun onUsernameChange(username: String) {
        updateState {
            val newState = this.copy(username = username)
            copy(
                username = username,
                isNextEnabled = newState.isValidInput()
            )
        }
    }

    override fun onClickNext() {
        sendNewEffect(EnterNameUIEffect.NavigateToNextStep)
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }
}