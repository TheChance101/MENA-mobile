package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

interface DukanCategoriesInteractionListener {
    fun onBackClicked()
    fun onCategoryClicked(categoryId: String)
    fun onDismissSnackBar()
}