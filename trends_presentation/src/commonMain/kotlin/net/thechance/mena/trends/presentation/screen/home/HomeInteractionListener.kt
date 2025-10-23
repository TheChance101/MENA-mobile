package net.thechance.mena.trends.presentation.screen.home

interface HomeInteractionListener {
    fun onClickLike(reelId: String)
    fun onClickAddReel()
    fun onClickEditTags()
    fun onClickManageMyTrends()
    fun onClickReel(reelId: String)
}