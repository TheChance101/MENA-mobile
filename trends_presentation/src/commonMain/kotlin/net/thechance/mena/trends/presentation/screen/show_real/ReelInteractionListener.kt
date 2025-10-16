package net.thechance.mena.trends.presentation.screen.show_real

interface ReelInteractionListener {
    fun onLikeClick(reelId: String)
    fun onAddReelClick()
    fun onEditTagsClick()
    fun onManageMyTrendsClick()
    fun onReelClick(reelId: String)
}