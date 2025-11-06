package net.thechance.mena.dukan.presentation.viewModel.mainScreen

interface MainInteractionListener {
    fun onDukanButtonClicked()
    fun onViewMoreClicked()
    fun onRetryClicked()
    fun onSnackBarDismissed()
    fun onSelectedCategoryClicked(categoryId: String, categoryName: String)

    fun onNearestDukanClicked(dukanId: String)

    fun onEditorPickDukanClicked(dukanId: String)
    fun onFavoriteDukanClicked(dukanId: String)
}