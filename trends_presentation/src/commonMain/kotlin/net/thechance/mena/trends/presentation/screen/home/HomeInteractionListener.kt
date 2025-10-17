package net.thechance.mena.trends.presentation.screen.home

interface HomeInteractionListener {
    fun onLikeClick(reelId: String)
    fun onAddReelClick()
    fun onEditTagsClick()
    fun onManageMyTrendsClick()
    fun onReelClick(reelId: String)
}