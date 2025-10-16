package net.thechance.mena.trends.presentation.screen.update_interests

internal interface UpdateInterestsInteractionListener {
    fun onCategoryClick(categoryId: String)
    fun onSaveClick()
    fun onBackClick()
}