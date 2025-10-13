package net.thechance.mena.dukan.presentation.viewModel.mainScreen

interface MainInteractionListener {
    fun onDukanButtonClicked()
    fun onViewMoreButtonClick()
    fun onCategorySelectedClick(categoryId: String, categoryName: String)

    fun onNearestDukanClick(dukanId: String)

    fun onEditorPickDukanClick(dukanId: String)
}