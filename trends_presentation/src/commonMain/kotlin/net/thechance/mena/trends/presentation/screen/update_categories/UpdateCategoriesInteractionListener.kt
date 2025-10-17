package net.thechance.mena.trends.presentation.screen.update_categories

internal interface UpdateCategoriesInteractionListener {
    fun onCategoryClick(categoryId: String)
    fun onSaveClick()
    fun onBackClick()
}