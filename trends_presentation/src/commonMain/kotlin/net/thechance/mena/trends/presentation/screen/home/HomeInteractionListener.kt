package net.thechance.mena.trends.presentation.screen.home

interface HomeInteractionListener {
    fun onClickLike(trendId: String, isLiked: Boolean)
    fun onClickAddTrend()
    fun onClickEditTags()
    fun onClickManageMyTrends()
    fun onClickTrend(trendId: String)
    fun onClickRetry()
    fun onClickExpandDescription(trendId: String)
    fun onGetRefreshedThumbnail(trendId: String)
}