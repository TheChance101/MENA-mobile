package net.thechance.mena.trends.presentation.screen.category_publish

interface CategoryPublishInteractionListener {
    fun onClickBack()
    fun onClickCategory(categoryId: String)
    fun onClickPublish()
    fun onClickRetry()
}