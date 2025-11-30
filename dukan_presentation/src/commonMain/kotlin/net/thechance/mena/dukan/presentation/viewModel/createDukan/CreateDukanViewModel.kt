package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.awaitCancellation
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_creation_failed
import mena.dukan_presentation.generated.resources.dukan_name_is_already_exist
import mena.dukan_presentation.generated.resources.error_image_size
import mena.dukan_presentation.generated.resources.error_upload_failed
import mena.dukan_presentation.generated.resources.invalid_image_format
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.exceptions.CreationFailedException
import net.thechance.mena.dukan.domain.exceptions.InvalidImageFormatException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.UploadingFailedException
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.util.imageCrop.toPngByteArray
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep
import org.jetbrains.compose.resources.StringResource
import org.maplibre.compose.camera.CameraPosition

class CreateDukanViewModel(
    private val dukanManagementRepository: DukanManagementRepository,
    private val locationRepository: LocationRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CreateDukanUiState, CreateDukanEffect>(
    initialState = CreateDukanUiState(),
    defaultDispatcher = defaultDispatcher
), CreateDukanInteractionListener {

    init {
        loadDukanCategories()
        getDukanStyle()
        getDukanColors()
    }

    override fun onNextOrCreateClicked() {
        if (state.value.currentStep != CreateDukanStep.SELECT_STYLE) {
            onNextClicked()
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
        if (state.value.isLocationPickerExpanded) {
            updateState { copy(isLocationPickerExpanded = false) }
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

    override fun onColorClicked(color: CreateDukanUiState.ColorUiState) {
        updateState { copy(selectedColor = color) }
        updateNextButtonEnableState()
    }

    override fun onStyleClicked(style: CreateDukanUiState.Style) {
        updateState { copy(selectedStyle = style) }
        updateNextButtonEnableState()
    }

    fun updateCreateButtonState(): Boolean {
        val state = state.value
        return state.selectedStyle != null && state.selectedColor != null
    }

    private fun getDukanColors() {
        tryToExecute(
            block = { dukanManagementRepository.getDukanColors() },
            onSuccess = ::updateScreenStateWithColors,
            onError = ::onErrorGettingStyles,
        )
    }

    private fun getDukanStyle() {
        tryToExecute(
            block = { dukanManagementRepository.getDukanStyles() },
            onSuccess = ::updateScreenStateWithStyles,
            onError = ::onErrorGettingStyles,
        )
    }

    private fun updateScreenStateWithStyles(dukanStyles: List<Dukan.Style>) {
        val stylesUiState = dukanStyles.map { style ->
            CreateDukanUiState.DukanStyleUiState(
                style = style.toUiStyle(),
                name = style.toUiStyleName()
            )
        }
        updateState { copy(dukanStyles = stylesUiState) }
    }

    private fun updateScreenStateWithColors(dukanColors: List<Color>) =
        updateState { copy(dukanColors = dukanColors.map { it.toUiColor() }) }

    override fun onClickUploadImage(image: ImageFile) {
        tryToExecute(
            block = { onUploadImageBlock(image) },
        )
    }

    private suspend fun onUploadImageBlock(image: ImageFile) {
        if (!isUploadImageValidToCrop(image))
            awaitCancellation()

        val imageSrc = image.toImageSrc()
        updateState {
            copy(
                selectedImage = imageSrc,
                isImageBeingCropped = true
            )
        }
    }

    private suspend fun isUploadImageValidToCrop(image: ImageFile): Boolean {
        val imageSizeInMegabyte = image.size().toDouble() / BYTES_PER_MEGABYTE
        val imageSrc = image.toImageSrc()

        return when {
            imageSizeInMegabyte > IMAGE_MAX_SIZE_IN_MB -> {
                showErrorUpload(Res.string.error_image_size)
                false
            }

            imageSrc == null -> {
                showErrorUpload(Res.string.error_upload_failed)
                false
            }

            else -> true
        }
    }

    private fun showErrorUpload(errorMessage: StringResource) {
        showSnackBar(message = errorMessage, type = SnackBarType.ERROR)
    }

    override fun onNextClicked() {
        val current = state.value.currentStep
        if (current == CreateDukanStep.BASIC_INFORMATION) {
            handleBasicInformationNext()
        } else {
            updateState { copy(currentStep = nextStep(current)) }
            updateNextButtonEnableState()
        }
    }

    override fun onDismissSnackBar() {
        updateState { copy(snackBarState = null) }
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
        updateState { copy(name = mapDukanNameToValidName(name), snackBarState = null) }
        updateNextButtonEnableState()
    }

    override fun isCategorySelected(): (CreateDukanUiState.DukanCategoryUiState) -> Boolean {
        return { category -> state.value.selectedCategories.contains(category) }
    }

    override fun onCategoryClicked(category: CreateDukanUiState.DukanCategoryUiState): Boolean {
        val isSelected = state.value.selectedCategories.contains(category)
        return if (isSelected) onCategoryDeselected(category)
        else onCategorySelected(category)

    }

    private fun onCategorySelected(category: CreateDukanUiState.DukanCategoryUiState): Boolean {
        if (!canSelectMoreCategories(state.value)) return false

        addCategoryToSelection(category)
        updateNextButtonEnableState()
        return true
    }

    private fun onCategoryDeselected(category: CreateDukanUiState.DukanCategoryUiState): Boolean {
        removeCategoryFromSelection(category)
        updateNextButtonEnableState()
        return true
    }

    override fun onCategoryEnabled(category: CreateDukanUiState.DukanCategoryUiState): Boolean {
        return canSelectMoreCategories(state.value) ||
                state.value.selectedCategories.contains(category)
    }

    private fun canSelectMoreCategories(currentState: CreateDukanUiState): Boolean {
        return currentState.selectedCategories.size < MAX_CATEGORIES
    }

    private fun addCategoryToSelection(category: CreateDukanUiState.DukanCategoryUiState) {
        updateState { copy(selectedCategories = selectedCategories + category) }
    }

    private fun removeCategoryFromSelection(category: CreateDukanUiState.DukanCategoryUiState) {
        updateState { copy(selectedCategories = selectedCategories - category) }
    }

    private fun onCreateClicked() {
        tryToExecute(
            onStart = { updateState { copy(isNextCreateButtonLoading = true) } },
            block = ::onCreateClickedBlock,
            onSuccess = ::onCreateClickedSuccess,
            onError = ::onErrorCreatingDukan

        )
    }

    private suspend fun onCreateClickedBlock() {
        dukanManagementRepository.createDukan(state.value.toEntity())
        state.value.croppedImage?.let {
            val fileName = state.value.name.replace(" ", "_")
                .plus("dukan_image")
            dukanManagementRepository.uploadDukanImage(fileName, it.toPngByteArray())
        }
    }

    private fun onCreateClickedSuccess(unit: Unit) {
        updateState { copy(isNextCreateButtonLoading = false) }
        emitEffect(CreateDukanEffect.NavigateToPending(state.value.name))
    }

    private fun handleBasicInformationNext() {
        if (!isBasicInformationStepValid(state.value)) {
            return
        }
        val trimmedName = state.value.name.trim()
        checkNameUniqueness(trimmedName)
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
    }

    private suspend fun onMapClickedBlock(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset
    ): String {
        updateState {
            copy(
                currentLocation = coordinates,
                pointerLocation = pointerLocation,
                cameraPosition = CameraPosition(
                    target = coordinates.toPosition(),
                    zoom = cameraPosition.zoom
                )
            )
        }
        return locationRepository.getCurrentLocationName(coordinates.toEntity())
    }

    private fun onMapClickedSuccess(address: String) = onAddressChanged(address)

    override fun onAddressChanged(address: String) {
        updateState { copy(address = address) }
        updateNextButtonEnableState()
    }

    override fun onCameraMoved(camera: CameraPosition) {
        updateState { copy(cameraPosition = camera) }
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

    override fun onExpandLocationPicker() {
        updateState { copy(isLocationPickerExpanded = true) }
    }

    override fun onConfirmLocationPicked() {
        updateState {
            copy(
                isLocationPickerExpanded = false,
                isMapLocked = true
            )
        }
    }

    override fun onCancelLocationPicker() {
        updateState { copy(isLocationPickerExpanded = false) }
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
            onStart = { updateState { copy(isNextCreateButtonLoading = true) } },
            block = { dukanManagementRepository.isDukanNameTaken(name) },
            onSuccess = { isTaken -> handleNameValidationResult(isTaken) },
            onError = ::onNameValidationError
        )
    }

    private fun mapDukanNameToValidName(name: String): String {
        val validDukanName = StringBuilder()
        var isPreviousCharWhitespace = false

        for (ch in name) {
            if (ch.isWhitespace()) {
                if (validDukanName.isEmpty() || isPreviousCharWhitespace) continue
                validDukanName.append(' ')
                isPreviousCharWhitespace = true
            } else {
                validDukanName.append(ch)
                isPreviousCharWhitespace = false
            }
            if (validDukanName.length >= MAX_NAME_LENGTH) break
        }

        return validDukanName.toString()
    }

    private fun handleNameValidationResult(isTaken: Boolean) {
        updateState { copy(isNextCreateButtonLoading = false) }
        val current = state.value.currentStep
        updateNameValidationState(isTaken, current)
        updateNextButtonEnableState()
    }

    private fun updateNameValidationState(isTaken: Boolean, current: CreateDukanStep) {
        if (isTaken) showSnackBar(
            message = Res.string.dukan_name_is_already_exist,
            type = SnackBarType.ERROR
        )
        updateState {
            copy(
                currentStep = if (isTaken) current else nextStep(current),
                isNameUnique = !isTaken
            )
        }
    }

    private fun onErrorGettingStyles(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun onErrorCreatingDukan(throwable: Throwable) {
        updateState { copy(isNextCreateButtonLoading = false) }
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            is CreationFailedException -> Res.string.dukan_creation_failed
            is UploadingFailedException -> Res.string.error_upload_failed
            is InvalidImageFormatException -> Res.string.invalid_image_format
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun onNameValidationError(throwable: Throwable) {
        updateState { copy(isNextCreateButtonLoading = false) }
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
        updateNextButtonEnableState()
    }

    private fun updateNextButtonEnableState() {
        val currentState = state.value
        val isNextButtonEnabled = when (currentState.currentStep) {
            CreateDukanStep.BASIC_INFORMATION -> isBasicInformationStepValid(currentState)
            CreateDukanStep.SELECT_IMAGE -> currentState.croppedImage != null
            CreateDukanStep.SELECT_LOCATION -> isLocationValid(currentState)
            CreateDukanStep.SELECT_STYLE -> updateCreateButtonState()
        }
        updateState { this.copy(isButtonEnabled = isNextButtonEnabled) }
    }

    private fun isBasicInformationStepValid(state: CreateDukanUiState): Boolean {
        return state.name.isNotBlank() &&
                state.selectedCategories.size in MIN_CATEGORIES..MAX_CATEGORIES &&
                state.snackBarState == null
    }

    private fun isLocationValid(currentState: CreateDukanUiState): Boolean {
        return currentState.address.isNotBlank()
                && currentState.currentLocation != CreateDukanUiState.CoordinatesUiState()
    }

    private fun loadDukanCategories() {
        tryToExecute(
            block = { dukanManagementRepository.getCategories() },
            onSuccess = { categories ->
                updateState { copy(dukanCategories = categories.toUiState()) }
            }
        )
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                )
            )
        }
    }

    private companion object {
        private const val MIN_CATEGORIES = 1
        private const val MAX_CATEGORIES = 3
        private const val MAX_NAME_LENGTH = 40
        const val BYTES_PER_MEGABYTE = 1024 * 1024
        const val IMAGE_MAX_SIZE_IN_MB = 5
    }
}