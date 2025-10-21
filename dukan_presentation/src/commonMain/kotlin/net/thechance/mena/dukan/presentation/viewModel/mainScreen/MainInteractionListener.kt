package net.thechance.mena.dukan.presentation.viewModel.mainScreen

interface MainInteractionListener {
    fun onDukanButtonClicked()
    fun onViewMoreButtonClick()
    fun onRetryButtonClicked()
    fun onDismissSnackBar()
    fun onCategorySelectedClick(categoryId: String, categoryName: String)

    fun onNearestDukanClick(dukanId: String)

    fun onEditorPickDukanClick(dukanId: String)
}