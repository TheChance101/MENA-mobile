package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class CreateDukanViewModel(
    private val dukanRepository: DukanRepository
) :
    BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    init {
        getDukanColors()
        getDukanStyle()
    }

    override fun onButtonClicked() {
        if (state.value.currentStep != SELECT_STYLE_INDEX) {
            onNextClicked()
        } else {
            onCreateClicked()
        }
    }

    override fun onBackClicked() {
        if (state.value.currentStep == BASIC_INFORMATION_INDEX) {

        } else {
            updateState { copy(currentStep = currentStep - 1) }
        }
        updateNextButtonEnableState()
    }

    override fun onColorClicked(color: Long) = updateState { copy(selectedColor = color) }

    override fun onStyleClicked(style: Dukan.Style) = updateState { copy(selectedStyle = style) }

    fun isButtonEnable() {
        val state = state.value
        if (state.selectedStyle != null && state.selectedColor != null) {
            updateState {
                copy(isButtonEnabled = true)
            }
        } else {
            updateState {
                copy(isButtonEnabled = false)
            }
        }
    }

    private fun getDukanColors() {
        tryToExecute(
            block = { dukanRepository.getDukanColors() },
            onSuccess = { updateState { copy(dukanColors = it) } },
            onError = { updateState { copy(errorMessage = it.message) } },
        )
    }

    private fun getDukanStyle() {
        tryToExecute(
            block = { dukanRepository.getDukanStyles() },
            onSuccess = { updateState { copy(dukanStyles = it) } },
            onError = { updateState { copy(errorMessage = it.message) } },
        )
    }

    private fun onCreateClicked() {
        TODO("Not yet implemented")
    }

    private fun onNextClicked() {
        updateState {
            copy(currentStep = currentStep + 1)
        }
        updateNextButtonEnableState()
    }

    private fun updateNextButtonEnableState() {
        val state = state.value
        val isNextButtonEnabled = when (state.currentStep) {
            BASIC_INFORMATION_INDEX -> true
            SELECT_IMAGE_INDEX -> true
            SELECT_LOCATION_INDEX -> true
            SELECT_STYLE_INDEX -> true
            else -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    companion object {
        const val MAX_STEPS = 4
        const val BASIC_INFORMATION_INDEX = 0
        const val SELECT_IMAGE_INDEX = 1
        const val SELECT_LOCATION_INDEX = 2
        const val SELECT_STYLE_INDEX = 3
    }
}