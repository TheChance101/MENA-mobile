package net.thechance.mena.dukan.presentation.viewModel.manageShelf


sealed interface ManageShelfEffect {
    data object NavigateBack : ManageShelfEffect
    data class NavigateBackWithShelfId(val shelfId: String) : ManageShelfEffect
    data object NavigateBackWithEditedShelfName : ManageShelfEffect
}