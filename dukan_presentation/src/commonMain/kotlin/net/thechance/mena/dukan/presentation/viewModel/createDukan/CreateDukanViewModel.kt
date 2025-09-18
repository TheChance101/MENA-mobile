package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep
import org.maplibre.compose.camera.CameraPosition

class CreateDukanViewModel(
    private val dukanRepository: DukanRepository,
    private val locationRepository: LocationRepository
) : BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    init {
        loadDukanCategories()
        getDukanStyle()
        getDukanColors()
    }

    override fun onButtonClicked() {
        if (state.value.currentStep != CreateDukanStep.SELECT_STYLE) {
            onCLickNext()
        } else {
            onCreateClicked()
        }
    }

    override fun onBackClicked() {
        val current = state.value.currentStep
        if (state.value.isImageBeingCropped) {
            updateState { copy(isImageBeingCropped = false) }
            return
        }
        if (current == CreateDukanStep.BASIC_INFORMATION) {
            emitEffect(CreateDukanEffect.NavigateBack)
        } else {
            updateState {
                copy(currentStep = previousStep(current))
            }
        }
        updateNextButtonEnableState()
    }

    override fun onColorClicked(color: ColorUiState) = updateState { copy(selectedColor = color) }
    override fun onStyleClicked(style: Dukan.Style) = updateState { copy(selectedStyle = style) }

    fun updateCreateButtonState() {
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
            onSuccess = ::updateScreenStateWithColors,
            onError = ::handleError,
        )
    }

    private fun getDukanStyle() {
        tryToExecute(
            block = { dukanRepository.getDukanStyles() },
            onSuccess = ::updateScreenStateWithStyles,
            onError = ::handleError,
        )
    }

    private fun updateScreenStateWithStyles(dukanStyles: List<Dukan.Style>) {
        val stylesUiState = dukanStyles.map { style ->
            DukanStyleUiState(
                style = style,
                name = style.toUiStyleName()
            )
        }
        updateState { copy(dukanStyles = stylesUiState) }
    }

    private fun updateScreenStateWithColors(dukanColors: List<Color>) =
        updateState { copy(dukanColors = dukanColors.map { it.toUiColor() }) }

    private fun handleError(throwable: Throwable) =
        updateState { copy(errorMessage = throwable.message) }

    override fun onClickUploadImage() {}

    override fun onClickUploadImage(image: ImageSrc) {
        updateState {
            copy(
                selectedImage = image,
                isImageBeingCropped = true
            )
        }
        updateNextButtonEnableState()
    }

    override fun onCLickNext() {
        val current = state.value.currentStep
        if (current == CreateDukanStep.BASIC_INFORMATION) {
            handleBasicInformationNext()
        } else {
            updateState { copy(currentStep = nextStep(current)) }
            updateNextButtonEnableState()
        }
    }

    override fun onImageCrop(image: ImageBitmap) {
        updateState {
            copy(
                croppedImage = image,
                selectedImage = null,
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

    override fun onNameChanged(name: String) {
        val limitedName = if (name.length > 40) name.take(40) else name
        updateState { copy(name = limitedName, showSnackBar = false) }
        updateNextButtonEnableState()
    }

    override fun isCategorySelected(): (DukanCategoryUiState) -> Boolean {
        return { category -> state.value.selectedCategories.contains(category) }
    }

    override fun onCategorySelected(category: DukanCategoryUiState): Boolean {
        if (!canSelectMoreCategories(state.value)) return false

        addCategoryToSelection(category)
        updateNextButtonEnableState()
        return true
    }

    override fun onCategoryDeselected(category: DukanCategoryUiState): Boolean {
        removeCategoryFromSelection(category)
        updateNextButtonEnableState()
        return true
    }

    override fun onCategoryEnabled(category: DukanCategoryUiState): Boolean {
        return canSelectMoreCategories(state.value) ||
                state.value.selectedCategories.contains(category)
    }

    private fun canSelectMoreCategories(currentState: CreateDukanUiState): Boolean {
        return currentState.selectedCategories.size < MAX_CATEGORIES
    }

    private fun addCategoryToSelection(category: DukanCategoryUiState) {
        updateState { copy(selectedCategories = selectedCategories + category) }
    }

    private fun removeCategoryFromSelection(category: DukanCategoryUiState) {
        updateState { copy(selectedCategories = selectedCategories - category) }
    }

    private fun onCreateClicked() {
        //TODO("Not yet implemented")
    }

    private fun handleBasicInformationNext() {
        if (!isBasicInformationStepValid(state.value)) {
            updateState { copy(showSnackBar = true, isNameUnique = false) }
            return
        }
        checkNameUniqueness(state.value.name)
    }

    private fun nextStep(step: CreateDukanStep): CreateDukanStep {
        return when (step) {
            CreateDukanStep.BASIC_INFORMATION -> CreateDukanStep.SELECT_IMAGE

            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.SELECT_LOCATION

            CreateDukanStep.SELECT_LOCATION -> {
                updateState { copy(isMapLocked = true) }
                CreateDukanStep.SELECT_STYLE
            }

            CreateDukanStep.SELECT_STYLE -> step
        }
    }

    override fun onMapClicked(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset
    ) {
        tryToExecute(
            block = { onMapClickedBlock(coordinates, pointerLocation) },
            onSuccess = ::onMapClickedSuccess
        )
        updateNextButtonEnableState()
    }

    override fun onCameraMoved(
        camera: CameraPosition
    ) {
        updateState { copy(cameraPosition = camera) }
    }

    private suspend fun onMapClickedBlock(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset
    ): String {
        updateState {
            copy(
                currentLocation = coordinates,
                pointerLocation = pointerLocation
            )
        }
        return locationRepository.getCurrentLocationName(coordinates.toEntity())
    }

    private fun onMapClickedSuccess(address: String) {
        updateState {
            copy(address = address)
        }
        updateNextButtonEnableState()
    }

    override fun onEditMapLocationClicked() {
        updateState {
            copy(
                address = "",
                currentLocation = CreateDukanUiState.CoordinatesUiState(),
                pointerLocation = null,
            )
        }
        updateNextButtonEnableState()
    }

    private fun previousStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> step
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.BASIC_INFORMATION
            CreateDukanStep.SELECT_LOCATION -> {
                updateState { copy(isMapLocked = state.value.pointerLocation != null) }
                CreateDukanStep.SELECT_IMAGE
            }

            CreateDukanStep.SELECT_STYLE -> CreateDukanStep.SELECT_LOCATION
        }

    private fun checkNameUniqueness(name: String) {
        tryToExecute(
            block = { dukanRepository.isDukanNameTaken(name) },
            onSuccess = { isTaken -> handleNameValidationResult(isTaken) },
            onError = { handleNameValidationError() }
        )
    }

    private fun handleNameValidationResult(isTaken: Boolean) {
        val current = state.value.currentStep
        updateNameValidationState(isTaken, current)
        updateNextButtonEnableState()
    }

    private fun updateNameValidationState(isTaken: Boolean, current: CreateDukanStep) {
        updateState {
            copy(
                isNameUnique = !isTaken,
                showSnackBar = isTaken,
                currentStep = if (isTaken) current else nextStep(current)
            )
        }
    }

    private fun handleNameValidationError() {
        updateState { copy(isNameUnique = false, showSnackBar = true) }
        updateNextButtonEnableState()
    }

    private fun updateNextButtonEnableState() {
        val currentState = state.value
        val isNextButtonEnabled = when (currentState.currentStep) {
            CreateDukanStep.BASIC_INFORMATION -> isBasicInformationStepValid(currentState)
            CreateDukanStep.SELECT_IMAGE -> currentState.croppedImage != null
            CreateDukanStep.SELECT_LOCATION -> currentState.address.isNotBlank()
            CreateDukanStep.SELECT_STYLE -> true
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    private fun isBasicInformationStepValid(state: CreateDukanUiState): Boolean {
        return state.name.isNotBlank() &&
                state.selectedCategories.size in MIN_CATEGORIES..MAX_CATEGORIES &&
                !state.showSnackBar
    }

    private fun loadDukanCategories() {
        tryToExecute(
            block = { dukanRepository.getCategories() },
            onSuccess = { categories ->
                updateState { copy(dukanCategories = categories.toUiState()) }
            }
        )
    }

    private companion object {
        private const val MIN_CATEGORIES = 1
        private const val MAX_CATEGORIES = 3
    }
}