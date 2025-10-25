package net.thechance.mena.trends.presentation.screen.update_categories

internal interface UpdateCategoriesInteractionListener {
    fun onClickCategory(categoryId: String)
    fun onClickSave()
    fun onClickBack()
    fun onClickRetry()
}