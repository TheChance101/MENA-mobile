package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesInteractionListener

object PreviewDukanCategoriesInteractionListener: DukanCategoriesInteractionListener {
    override fun onBackClicked() {}
    override fun onCategoryClicked(categoryName: String, categoryId: String) {}
    override fun onDismissSnackBar() {}
    override fun onRetryClicked() {}
}