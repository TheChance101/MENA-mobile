package net.thechance.mena.dukan.presentation.viewModel.createShelf

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.failed_to_create_shelf
import mena.dukan_presentation.generated.resources.no_internet_message
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import mena.dukan_presentation.generated.resources.shelf_name_is_invalid
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.CreationFailedException
import net.thechance.mena.dukan.domain.exceptions.DuplicateNameException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class CreateShelfViewModel(
    private val shelfRepository: ShelfRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CreateShelfUiState, CreateShelfEffect>(
    initialState = CreateShelfUiState(),
    defaultDispatcher = defaultDispatcher
), CreateShelfInteractionListener {

    override fun onTitleChanged(shelfTitle: String) {
        val trimmed = shelfTitle.trim()
        val valid = trimmed.isNotBlank() && validTitleRegex.matches(trimmed)
        updateState {
            copy(
                shelfTitle = trimmed,
                isCreateButtonEnabled = valid
            )
        }
    }

    override fun onBackButtonClicked() {
        emitEffect(CreateShelfEffect.NavigateBack)
    }


    override fun onCreateButtonClicked() {
        val title = state.value.shelfTitle
        if (!isTitleValid(title)) {
            showSnackBar(message = Res.string.shelf_name_is_invalid, type = SnackBarType.ERROR)
            return
        }

        tryToExecute(
            onStart = ::onCreateClickedStart,
            block = { shelfRepository.createShelf(Shelf(id = "", name = title)) },
            onSuccess = { onCreateShelfSuccess() },
            onError = ::onCreateShelfError
        )
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(snackBarState = null)
        }
    }

    private fun isTitleValid(title: String): Boolean {
        return title.isNotBlank() && validTitleRegex.matches(title)
    }

    private fun onCreateClickedStart() {
        updateState { copy(isLoading = true) }
    }

    private fun onCreateShelfSuccess() {
        updateState { copy(isLoading = false) }
        emitEffect(CreateShelfEffect.NavigateToManageDukan)

    }

    private fun onCreateShelfError(throwable: Throwable) {
        val messageRes = when (throwable) {
            is CreationFailedException -> Res.string.failed_to_create_shelf
            is DuplicateNameException -> Res.string.shelf_name_is_already_exist
            is NoInternetException -> Res.string.no_internet_message
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
        updateState { copy(isLoading = false) }
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    snackBarType = type,
                    message = message
                )
            )
        }
    }


    companion object {
        private val validTitleRegex = Regex("^[\\p{L}\\s-]+$")
    }
}
