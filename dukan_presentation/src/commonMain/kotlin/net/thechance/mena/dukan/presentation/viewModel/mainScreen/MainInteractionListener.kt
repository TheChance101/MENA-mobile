package net.thechance.mena.dukan.presentation.viewModel.mainScreen

interface MainInteractionListener {
    fun onDukanButtonClicked()
    fun onViewMoreClicked()
    fun onRetryClicked()
    fun onDismissSnackBar()
    fun onCategorySelectedClicked(categoryId: String, categoryName: String)

    fun onNearestDukanClicked(dukanId: String)

    fun onEditorPickDukanClicked(dukanId: String)
}