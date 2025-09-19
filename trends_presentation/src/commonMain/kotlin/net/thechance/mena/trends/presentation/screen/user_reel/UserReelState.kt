package net.thechance.mena.trends.presentation.screen.user_reel

data class UserReelState(
    val id: String? = null,
    val username: String = "",
    val thumbnail: String = "",
    val createdAt: String = "",
    val isLoading: Boolean = false,
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val description: String = "",
    val isConfirmationDialogVisible: Boolean = false,
    val isReelDeleted: Boolean? = null,
    val isDescriptionExpanded: Boolean = false
)