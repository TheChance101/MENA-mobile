package net.thechance.mena.dukan.presentation.viewModel.createShelf

interface CreateShelfInteractionListener {
    fun onTitleChanged(shelfTitle: String)
    fun onBackClicked()
    fun onCreateClicked()
    fun onDismissSnackBar()
}