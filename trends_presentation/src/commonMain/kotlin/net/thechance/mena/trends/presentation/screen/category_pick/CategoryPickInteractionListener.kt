package net.thechance.mena.trends.presentation.screen.category_pick

internal interface CategoryPickInteractionListener {
    fun onCategoryClick(categoryId: String)
    fun onNextClick()
    fun onBackClick()
}