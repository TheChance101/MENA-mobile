package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.CreateDukanStep

class CreateDukanViewModel(
    private val dukanRepository: DukanRepository
) :
    BaseViewModel<CreateDukanUiState, CreateDukanEffect>(CreateDukanUiState()),
    CreateDukanInteractionListener {

    init {
        loadDukanCategories()
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

    private fun nextStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> CreateDukanStep.SELECT_IMAGE
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.SELECT_LOCATION
            CreateDukanStep.SELECT_LOCATION -> CreateDukanStep.SELECT_STYLE
            CreateDukanStep.SELECT_STYLE -> step
        }

    private fun previousStep(step: CreateDukanStep): CreateDukanStep =
        when (step) {
            CreateDukanStep.BASIC_INFORMATION -> step
            CreateDukanStep.SELECT_IMAGE -> CreateDukanStep.BASIC_INFORMATION
            CreateDukanStep.SELECT_LOCATION -> CreateDukanStep.SELECT_IMAGE
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
            CreateDukanStep.SELECT_LOCATION -> true
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