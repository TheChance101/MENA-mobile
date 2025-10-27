package net.thechance.mena.trends.presentation.screen.category_pick

internal interface CategoryPickInteractionListener {
    fun onClickCategory(categoryId: String)
    fun onClickNext()
    fun onClickBack()
    fun onClickRetry()
}