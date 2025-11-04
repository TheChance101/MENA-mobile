package net.thechance.mena.identity.presentation.screen.register.enterName

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.presentation.base.BaseScreenModel

class EnterNameViewModel(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<EnterNameUIState, EnterNameUIEffect>
    (EnterNameUIState()),
    EnterNameInteractionListener {

    private fun validateInputs(state: EnterNameUIState): Boolean {
        return state.firstName.isNotBlank() && state.lastName.isNotBlank() && state.username.isNotBlank()
    }

    override fun onFirstNameChange(name: String) {
        updateState {
            copy(
                firstName = name,
                isNextEnabled = validateInputs(this.copy(firstName = name))
            )
        }
    }

    override fun onLastNameChange(name: String) {
        updateState {
            copy(
                lastName = name,
                isNextEnabled = validateInputs(this.copy(lastName = name))
            )
        }
    }

    override fun onUsernameChange(username: String) {
        updateState {
            copy(
                username = username,
                isNextEnabled = validateInputs(this.copy(username = username))
            )
        }
    }

    override fun onClickNext() {
        sendNewEffect(EnterNameUIEffect.NavigateToNextStep)
    }

    override fun onClickBack() {
        sendNewEffect(EnterNameUIEffect.NavigateBack)
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }
}