package net.thechance.mena.trends.presentation.screen.manage_my_trends

internal interface ManageTrendsInteractionListener {
    fun onClickReel(reelId: String)
    fun onClickBack()
    fun onClickRetry()
    fun onSelectTab(tab: SelectTab)
}