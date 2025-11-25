package net.thechance.mena.trends.presentation.screen.manage_my_trends

internal interface ManageTrendsInteractionListener {
    fun onClickTrend(trend: String)
    fun onClickBack()
    fun onClickRetry()
    fun onSelectTab(tab: SelectTab)
    fun onGetRefreshedThumbnail(trendId: String)
}