package net.thechance.mena.dukan.presentation.viewModel.manageShelf

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ManageShelfViewModel(
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageShelfUiState, ManageShelfEffect>(
    initialState = ManageShelfUiState(), defaultDispatcher = defaultDispatcher
), ManageShelfInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ManageShelfScreenRoute>()


    init {
        updateState { copy(shelfTitle = args.shelfTitle) }
    }

    override fun onBackClicked() {
        emitEffect(ManageShelfEffect.NavigateBack)
    }

    override fun onDeleteClicked() {
        emitEffect(ManageShelfEffect.NavigateBackWithShelfId(args.shelfId))
    }
}