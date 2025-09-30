package net.thechance.mena.dukan.presentation.viewModel.createShelf

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import mena.dukan_presentation.generated.resources.shelf_name_is_invalid
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.CreateShelfRepository
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class CreateShelfViewModel(
    private val shelfRepository: CreateShelfRepository,
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

    override fun onCreateButtonClicked() {
        val title = state.value.shelfTitle
        if (!isTitleValid(title)) {
            showSnackBar(message = Res.string.shelf_name_is_invalid, type = SnackBarType.ERROR)
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
            shelfRepository.createShelf(Shelf(id = "", name = title))
            true
        } else {
            false
        }
    }

    private fun onCreateClickedSuccess(isCreated: Boolean) {
        updateState { copy(isLoading = false) }
        if (isCreated) {
            emitEffect(CreateShelfEffect.NavigateToApprovedDukan)
        } else {
            showSnackBar(message = Res.string.shelf_name_is_already_exist, type = SnackBarType.ERROR)
        }
    }

    private fun onCreateClickedError() {
        updateState { copy(isLoading = false) }
        showSnackBar(message = Res.string.shelf_name_is_already_exist, type = SnackBarType.ERROR)
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(snackBarState = null)
        }
    }

    companion object {
        private val validTitleRegex = Regex("^[\\p{L}\\s-]+$")
    }
}
