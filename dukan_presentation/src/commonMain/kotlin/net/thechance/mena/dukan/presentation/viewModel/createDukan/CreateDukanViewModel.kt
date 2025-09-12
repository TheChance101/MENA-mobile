package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class CreateDukanViewModel :
    BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    override fun onButtonClicked() {
        if(state.value.currentStep != 4) {
            onNextClicked()
        } else {
            onCreateClicked()
        }
    }

    override fun onBackClicked() {
        if (state.value.currentStep == 1) {

        } else {
            updateState { this.copy(currentStep = this.currentStep - 1) }
        }
        updateNextButtonEnableState()
    }

    private fun onCreateClicked() {
        TODO("Not yet implemented")
    }

    private fun onNextClicked() {
        println("ViewModelTest ${state.value.currentStep}")
        updateState {
            this.copy(currentStep = this.currentStep + 1)
        }
        println("ViewModelTest ${state.value.currentStep}")
        updateNextButtonEnableState()
    }

    private fun updateNextButtonEnableState() {
        val state = state.value
        val isNextButtonEnabled = when (state.currentStep) {
            1 -> true
            2 -> true
            3 -> true
            4 -> true
            else -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    companion object {
        const val MAX_STEPS = 4
    }
}