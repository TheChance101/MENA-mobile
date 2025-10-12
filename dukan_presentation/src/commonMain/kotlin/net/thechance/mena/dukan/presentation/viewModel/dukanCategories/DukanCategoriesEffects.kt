package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

interface DukanCategoriesEffects {
    object NavigateBack : DukanCategoriesEffects
    data class NavigateToDukansOfCategory(
        val categoryName: String,
        val categoryId: String
    ) : DukanCategoriesEffects
}