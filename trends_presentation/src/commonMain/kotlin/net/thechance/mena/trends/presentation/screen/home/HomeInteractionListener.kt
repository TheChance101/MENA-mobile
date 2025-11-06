package net.thechance.mena.trends.presentation.screen.home

interface HomeInteractionListener {
    fun onClickLike(reelId: String, isLiked: Boolean)
    fun onClickAddReel()
    fun onClickEditTags()
    fun onClickManageMyTrends()
    fun onClickReel(reelId: String)
    fun onClickRetry()
    fun onClickExpandDescription(reelId: String)
    fun onGetRefreshedThumbnail(reelId: String)
}