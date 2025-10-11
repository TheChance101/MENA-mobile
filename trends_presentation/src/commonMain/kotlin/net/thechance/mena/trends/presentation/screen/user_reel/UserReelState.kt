package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.trends.presentation.shared.base.ErrorState

internal data class UserReelState(
    val reels: Flow<PagingData<UserReelUiState>> = flowOf(),
    val isLoading: Boolean = false,
    val error: ErrorState? = null,
    val isConfirmationDialogVisible: Boolean = false,
    val isReelDeleted: Boolean? = null,
    val isDescriptionExpanded: Boolean = false,
)

data class UserReelUiState( //TODO get user info
    val id : String = "",
    val videoUrl: String= "",
    val description: String = "",
    val likesCount: Int = 0,
    val viewsCount: Int = 0,
    val username : String = "",
    val profileImage : String = "",
    val createdAt: String?= null,
    val isCurrentUserOwner: Boolean = false
)