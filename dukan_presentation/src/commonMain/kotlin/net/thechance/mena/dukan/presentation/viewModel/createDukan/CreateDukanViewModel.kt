package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep
import org.maplibre.compose.expressions.dsl.Feature.state

class CreateDukanViewModel(
    private val locationRepository: LocationRepository
) : BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    override fun onButtonClicked() {
        if (state.value.currentStep != CreateDukanUiState.CreateDukanStep.SELECT_STYLE) {
            onNextClicked()
        } else {
            onCreateClicked()
        }
    }

    override fun onBackClicked() {
        val current = state.value.currentStep
        if (current == CreateDukanStep.BASIC_INFORMATION) {
            // maybe do nothing or exit flow
        } else {
            updateState {
                copy(currentStep = previousStep(current))
            }
        }
        updateNextButtonEnableState()
    }

    override fun onMapClicked(coordinates: CreateDukanUiState.CoordinatesUi) {
        tryToExecute(
            block = { onMapClickedBlock(coordinates) },
            onSuccess = ::onMapClickedSuccess
        )
        updateNextButtonEnableState()
    }

    private suspend fun onMapClickedBlock(coordinates: CreateDukanUiState.CoordinatesUi): String {
        updateState { copy(currentLocation = coordinates) }
        return locationRepository.getCurrentLocationName(coordinates.toEntity())
    }

    private fun onMapClickedSuccess(address: String) {
        updateState {
            copy(address = address)
        }
    }

    override fun onEditClicked() {
        updateState {
            copy(
                isMapLocked = false,
                address = "",
                currentLocation = CreateDukanUiState.CoordinatesUi()
            )
        }
        updateNextButtonEnableState()
    }

    private fun onCreateClicked() {
        TODO("Not yet implemented")
    }

    private fun onNextClicked() {
        val current = state.value.currentStep
        nextStep(current)
        updateNextButtonEnableState()
    }

    private fun nextStep(step: CreateDukanStep) {
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> {
                updateState { copy(currentStep = CreateDukanStep.SELECT_IMAGE) }
            }

            CreateDukanStep.SELECT_IMAGE -> {
                updateState { copy(currentStep = CreateDukanStep.SELECT_LOCATION) }
            }

            CreateDukanStep.SELECT_LOCATION -> {
                updateState { copy(currentStep = CreateDukanStep.SELECT_STYLE) }
                loadCurrentLocation()
            }

            CreateDukanStep.SELECT_STYLE -> {
                step
            }
        }
    }

    private fun loadCurrentLocation() {
        tryToExecute(
            block = locationRepository::getCurrentLocation,
            onSuccess = ::onLoadCurrentLocationSuccessfully
        )
    }

    private fun onLoadCurrentLocationSuccessfully(location: Dukan.Coordinates) {
        updateState { copy(currentLocation = location.toUiState()) }
    }

    private fun previousStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> step
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.BASIC_INFORMATION
            CreateDukanStep.SELECT_LOCATION -> CreateDukanStep.SELECT_IMAGE
            CreateDukanStep.SELECT_STYLE -> CreateDukanStep.SELECT_LOCATION
        }

    private fun updateNextButtonEnableState() {
        val state = state.value
        val isNextButtonEnabled = when (state.currentStep) {
            CreateDukanStep.BASIC_INFORMATION -> true
            CreateDukanStep.SELECT_IMAGE -> true
            CreateDukanStep.SELECT_LOCATION -> state.address.isNotEmpty()
            CreateDukanStep.SELECT_STYLE -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }
}