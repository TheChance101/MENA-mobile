package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

interface DukanCategoriesInteractionListener {
    fun onBackClicked()
    fun onCategoryClicked(categoryName: String, categoryId: String)
    fun onDismissSnackBar()
    fun onRetryClicked()
}