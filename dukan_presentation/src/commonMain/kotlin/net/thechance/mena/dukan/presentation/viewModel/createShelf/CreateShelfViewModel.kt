package net.thechance.mena.dukan.presentation.viewModel.createShelf

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.failed_to_create_shelf
import mena.dukan_presentation.generated.resources.shelf_name_already_exists
import mena.dukan_presentation.generated.resources.shelf_name_is_invalid
import net.thechance.mena.dukan.domain.entity.Shelf
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
        updateState { copy(showShelfAddedSuccess = false) }
        emitEffect(CreateShelfEffect.NavigateBack)
    }

    override fun onCreateButtonClicked() {
        val title = state.value.shelfTitle
        if (!isTitleValid(title)) {
            showSnackBar(Res.string.shelf_name_is_invalid)
            return
        }

        tryToExecute(
            onStart = { onCreateClickedStart() },
            block = { onCreateClickedBlock(title) },
            onSuccess = { isCreated -> onCreateClickedSuccess(isCreated) },
            onError = { onCreateClickedError() }
        )
    }

    private fun isTitleValid(title: String): Boolean {
        return title.isNotBlank() && validTitleRegex.matches(title)
    }

    private fun onCreateClickedStart() {
        updateState { copy(isLoading = true) }
    }

    private suspend fun onCreateClickedBlock(title: String): Boolean {
        val allShelves = shelfRepository.getMyDukanShelves()
        val nameExists = allShelves.any { it.name.equals(title, ignoreCase = true) }

        return if (!nameExists) {
            shelfRepository.createShelf(Shelf(id = "", name = title, dukanId = ""))
            true
        } else {
            false
        }
    }

    private fun onCreateClickedSuccess(isCreated: Boolean) {
        updateState { copy(isLoading = false) }
        if (isCreated) {
            updateState { copy(showShelfAddedSuccess = true) }
            emitEffect(CreateShelfEffect.NavigateToApprovedDukan)
        } else {
            showSnackBar(Res.string.shelf_name_already_exists)
        }
    }

    private fun onCreateClickedError() {
        updateState { copy(isLoading = false) }
        showSnackBar(Res.string.failed_to_create_shelf)
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(
                showSnackBar = false,
                snackBarState = null
            )
        }
    }

    fun showSnackBar(
        message: StringResource,
        type: SnackBarType = SnackBarType.ERROR
    ) {
        updateState {
            copy(
                showSnackBar = true,
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
