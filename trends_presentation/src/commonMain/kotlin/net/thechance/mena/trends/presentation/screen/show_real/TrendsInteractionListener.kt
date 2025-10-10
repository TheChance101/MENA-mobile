package net.thechance.mena.trends.presentation.screen.show_real

interface TrendsInteractionListener {
    fun onMoreClick()
    fun onLikeClick(reelId: String)
    fun onAddReelClick()
    fun onEditTagsClick()
    fun onManageMyTrendsClick()
    fun onReelClick(reelId: String)
}