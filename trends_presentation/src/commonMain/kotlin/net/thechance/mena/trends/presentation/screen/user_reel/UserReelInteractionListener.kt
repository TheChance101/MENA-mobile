package net.thechance.mena.trends.presentation.screen.user_reel

internal interface UserReelInteractionListener {
    fun onBackClick()
    fun onDeleteClick()
    fun onConfirmDeleteClick()
    fun onDismissConfirmationDialog()
    fun onDismissSuccessDialog()
    fun onDismissErrorDialog()
    fun onDescriptionClick(isCollapsed: Boolean)
    fun onPublisherInfoClick()
    fun increaseReelView(reelId: String)
    fun onLikeClick(reelId: String)
}