package net.thechance.mena.trends.presentation.screen.user_reel

internal interface UserReelInteractionListener {
    fun onClickBack()
    fun onChangeCurrentReel(reelId: String)
    fun onClickDelete()
    fun onClickConfirmDelete()
    fun onDismissConfirmationDialog()
    fun onDismissSuccessDialog()
    fun onDismissErrorDialog()
    fun onClickDescription(isCollapsed: Boolean)
    fun onClickPublisherInfo()
    fun increaseReelView(reelId: String)
    fun onClickLike(reelId: String, isLiked: Boolean)
    fun onGetRefreshVideoUrl(reelId: String)
}