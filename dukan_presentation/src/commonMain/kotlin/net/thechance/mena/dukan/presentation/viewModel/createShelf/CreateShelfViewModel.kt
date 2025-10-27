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
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateShelfViewModel(
    private val shelfRepository: ShelfRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CreateShelfUiState, CreateShelfEffect>(
    initialState = CreateShelfUiState(),
    defaultDispatcher = defaultDispatcher
), CreateShelfInteractionListener {

    override fun onTitleChanged(shelfTitle: String) {
        updateState {
            copy(
                shelfTitle = shelfTitle,
                isCreateButtonEnabled = shelfTitle.isNotBlank()
            )
        }
    }

    override fun onBackClicked() {
        emitEffect(CreateShelfEffect.NavigateBack)
    }


    @OptIn(ExperimentalUuidApi::class)
    override fun onCreateClicked() {
        val trimmedShelfTitle = state.value.shelfTitle.trim()
        if (!isTitleValid(trimmedShelfTitle)) {
            showErrorSnackBar(message = Res.string.shelf_name_is_invalid)
            return
        }

        tryToExecute(
            onStart = ::onCreateClickedStart,
            block = { shelfRepository.createShelf(Shelf(id = Uuid.random(), name = trimmedShelfTitle)) },
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
        return title.isNotBlank() && validShelfTitleRegex.matches(title)
    }

    private fun onCreateClickedStart() {
        updateState { copy(isLoading = true) }
    }

    private fun onCreateShelfSuccess() {
        updateState { copy(isLoading = false) }
        emitEffect(effect = CreateShelfEffect.NavigateToManageDukan)
    }

    private fun onCreateShelfError(throwable: Throwable) {
        val messageRes = when (throwable) {
            is CreationFailedException -> Res.string.failed_to_create_shelf
            is DuplicateNameException -> Res.string.shelf_name_is_already_exist
            is NoInternetException -> Res.string.no_internet_message
            else -> Res.string.something_went_wrong
        }
        showErrorSnackBar(message = messageRes)
        updateState { copy(isLoading = false) }
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
