package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.trends.presentation.shared.base.ErrorState

internal data class UserReelState(
    val reels: Flow<PagingData<UserReelUiState>> = flowOf(),
    val isLoading: Boolean = false,
    val error: ErrorState? = null,
    val id: String? = null,
    val username: String = "",
    val thumbnail: String = "",
    val createdAt: String = "",
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val description: String = "",
    val isConfirmationDialogVisible: Boolean = false,
    val isReelDeleted: Boolean? = null,
    val isDescriptionExpanded: Boolean = false,
)

data class UserReelUiState(
    val id: String,
    val videoUrl: String,
    val description: String,
    val likesCount: Int,
    val viewsCount: Int,
    val createdAt: String,
    val isCurrentUserOwner: Boolean
)