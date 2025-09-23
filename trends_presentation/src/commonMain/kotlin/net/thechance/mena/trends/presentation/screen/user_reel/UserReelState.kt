package net.thechance.mena.trends.presentation.screen.user_reel

import net.thechance.mena.trends.presentation.shared.base.ErrorState

internal data class UserReelState(
    val isLoading: Boolean = false,
    val error: ErrorState? = null,
    val id: String? = null,
    val username: String = "Istanbul Squad",
    val thumbnail: String = "https://images.pexels.com/photos/1054655/pexels-photo-1054655.jpeg?cs=srgb&dl=pexels-hsapir-1054655.jpg&fm=jpg",
    val createdAt: String = "2 hours ago",
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val description: String = "This is a sample description for the reel. It can be expanded or collapsed based on user interaction",
    val isConfirmationDialogVisible: Boolean = false,
    val isReelDeleted: Boolean? = null,
    val isDescriptionExpanded: Boolean = false
)