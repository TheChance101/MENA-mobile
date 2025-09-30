package net.thechance.mena.trends.presentation.screen.category_publish

interface CategoryPublishInteractionListener {
    fun onBackClick()
    fun onCategoryClick(categoryId: String)
    fun onPublishClick()
}