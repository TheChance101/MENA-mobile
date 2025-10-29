package net.thechance.mena.dukan.presentation.viewModel.manageShelf

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.error_edit_shelf
import mena.dukan_presentation.generated.resources.error_same_name_of_shelf
import mena.dukan_presentation.generated.resources.no_internet_message
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import mena.dukan_presentation.generated.resources.shelf_name_is_invalid
import net.thechance.mena.dukan.domain.exceptions.DuplicateNameException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class ManageShelfViewModel(
    private val shelfRepository: ShelfRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageShelfUiState, ManageShelfEffect>(
    initialState = ManageShelfUiState(), defaultDispatcher = defaultDispatcher
), ManageShelfInteractionListener {
    val shelfId: String = requireNotNull(savedStateHandle[ManageShelfArgs.shelfId])

    init {
        val shelfTitle: String = savedStateHandle[ManageShelfArgs.shelfTitle] ?: ""
        updateState { copy(oldShelfTitle = shelfTitle) }
    }

    override fun onBackClicked() {
        emitEffect(ManageShelfEffect.NavigateBack)
    }

    override fun onDeleteClicked() {
        emitEffect(ManageShelfEffect.NavigateBackWithShelfId(shelfId))
    }

    override fun onShelfNameChange(name: String) {
        updateState {
            copy(
                shelfTitle = name,
                isSaveButtonEnabled = name.isNotBlank()
            )
        }
    }

    override fun onSaveClicked() {
        val trimmedTitle = validateShelfTitle() ?: return
        tryToExecute(
            block = { updateShelfName(shelfId, trimmedTitle) },
            onSuccess = { onEditShelfSuccess() },
            onError = ::onEditShelfError
        )
    }

    private fun validateShelfTitle(): String? {
        val trimmedTitle = state.value.shelfTitle.trim()

        return when {
            !isTitleValid(trimmedTitle) -> {
                showErrorSnackBar(Res.string.shelf_name_is_invalid)
                null
            }

            state.value.oldShelfTitle == state.value.shelfTitle -> {
                showErrorSnackBar(Res.string.error_same_name_of_shelf)
                null
            }

            else -> trimmedTitle
        }
    }

    private suspend fun updateShelfName(shelfId: String, trimmedTitle: String) {
        shelfRepository.updateShelf(
            shelfId = shelfId,
            newShelfName = trimmedTitle
        )
    }

    override fun onDismissSnackBar() {
        updateState { copy(snackBarState = null) }
    }

    private fun isTitleValid(title: String): Boolean {
        return title.isNotBlank() && validShelfTitleRegex.matches(title)
    }

    private fun onEditShelfSuccess() {
        emitEffect(ManageShelfEffect.NavigateBackWithEditedShelfName)
    }

    private fun onEditShelfError(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_message
            is DuplicateNameException -> Res.string.shelf_name_is_already_exist
            else -> Res.string.error_edit_shelf
        }
        showErrorSnackBar(messageRes)
    }

    private fun showErrorSnackBar(message: StringResource) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    snackBarType = SnackBarType.ERROR,
                    message = message
                )
            )
        }
    }

    companion object {
        private val validShelfTitleRegex = Regex("^[\\p{L}\\s-]+$")
    }
}
