package net.thechance.mena.dukan.presentation.viewModel.manageShelf


sealed interface ManageShelfEffect {
    data object NavigateBack : ManageShelfEffect
    data class DeleteShelf(val shelfId: String) : ManageShelfEffect
}