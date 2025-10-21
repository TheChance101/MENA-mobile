package net.thechance.mena.dukan.presentation.viewModel.manageShelf

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ManageShelfViewModel(
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageShelfUiState, ManageShelfEffect>(
    initialState = ManageShelfUiState(), defaultDispatcher = defaultDispatcher
), ManageShelfInteractionListener {
    val shelfId: String = requireNotNull(savedStateHandle[ManageShelfArgs.shelfId])

    init {
        val shelfTitle: String = savedStateHandle[ManageShelfArgs.shelfTitle] ?: ""
        updateState { copy(shelfTitle = shelfTitle) }
    }

    override fun onBackClicked() {
        emitEffect(ManageShelfEffect.NavigateBack)
    }

    override fun onDeleteClicked() {
        emitEffect(ManageShelfEffect.NavigateBackWithShelfId(shelfId))
    }
}