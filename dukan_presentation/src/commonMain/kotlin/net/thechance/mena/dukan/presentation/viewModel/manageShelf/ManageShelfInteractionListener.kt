package net.thechance.mena.dukan.presentation.viewModel.manageShelf

interface ManageShelfInteractionListener {
    fun onBackClicked()
    fun onDeleteClicked()
    fun onShelfNameChange(name: String)
    fun onSaveClicked()
    fun onDismissSnackBar()
}