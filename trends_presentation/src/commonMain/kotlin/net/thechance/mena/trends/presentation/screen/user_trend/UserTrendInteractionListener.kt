package net.thechance.mena.trends.presentation.screen.user_trend

internal interface UserTrendInteractionListener {
    fun onClickBack()
    fun onChangeCurrentTrend(trendId: String)
    fun onClickDelete()
    fun onClickConfirmDelete()
    fun onDismissConfirmationDialog()
    fun onDismissSuccessDialog()
    fun onDismissErrorDialog()
    fun onClickDescription(isCollapsed: Boolean)
    fun onClickPublisherInfo()
    fun increaseTrendView(trendId: String)
    fun onClickLike(trendId: String, isLiked: Boolean)
    fun onGetRefreshVideoUrl(trendId: String)
    fun saveUserTrendEngagement(trendWatchSessionState: TrendWatchSessionState, trendId: String)
    fun onClickRetry(trendId: String)
    fun onNetworkError()
}