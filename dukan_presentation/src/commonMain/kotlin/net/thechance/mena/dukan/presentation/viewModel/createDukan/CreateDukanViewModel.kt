package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository
import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep
import org.maplibre.compose.expressions.dsl.Feature.state
import org.maplibre.compose.sources.SourceDefaults.MAX_ZOOM
import org.maplibre.compose.sources.SourceDefaults.MIN_ZOOM

class CreateDukanViewModel(
    private val locationRepository: LocationRepository
) : BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    override fun onButtonClicked() {
        if (state.value.currentStep != CreateDukanUiState.CreateDukanStep.SELECT_STYLE) {
            onCLickNext()
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

    override fun onClickUploadImage(
        image: ImageSrc
    ) {
        updateState {
            copy(
                selectedImage = image,
                isEditIconVisible = false,
                isImageBeingCropped = true
            )
        }
        updateNextButtonEnableState()
    }

    override fun onClickEditImage() {
        emitEffect(CreateDukanEffect.NavigateToImageCropScreen)
    }

    override fun onCLickNext() {
        val current = state.value.currentStep
        nextStep(current)
    }

    override fun onImageCrop(image: ImageBitmap) {
        updateState {
            copy(
                croppedImage = image,
                selectedImage = null,
                isEditIconVisible = true,
                isImageBeingCropped = false
            )
        }
        updateNextButtonEnableState()
    }

    override fun onCancelCrop() {
        updateState {
            copy(
                selectedImage = null,
                isImageBeingCropped = false
            )
        }
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
            CreateDukanStep.SELECT_IMAGE -> state.croppedImage != null
            CreateDukanStep.SELECT_LOCATION -> state.address.isNotEmpty()
            CreateDukanStep.SELECT_STYLE -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    private companion object {
        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 4f
        private const val ZOOM_STEP = 0.25f
    }
}