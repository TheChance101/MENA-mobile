package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.util.TimeAgoValue

internal data class UserReelState(
    val reels: Flow<PagingData<UserReelUiState>> = flowOf(),
    val isLoading: Boolean = false,
    val currentReelId: String = "",
    val error: ErrorState? = null,
    val isConfirmationDialogVisible: Boolean = false,
    val isReelDeleted: Boolean? = null,
    val isDescriptionExpanded: Boolean = false,
    val reelsStateFlow: MutableStateFlow<PagingData<UserReelUiState>> = MutableStateFlow(PagingData.empty())
)

internal data class UserReelUiState(
    val id: String = "",
    val videoUrl: String = "",
    val description: String = "",
    val likesCount: Int = 0,
    val viewsCount: Int = 0,
    val username: String = "",
    val profileImageUrl: String = "",
    val createdAt: TimeAgoValue? = null,
    val isCurrentUserOwner: Boolean = false,
    val isLiked: Boolean = false
)

data class ReelWatchSessionState(
    var reelId: String = "",
    var watchStartTime: LocalDateTime? = null,
    var watchEndTime: LocalDateTime? = null,
    var videoDurationInMilliseconds: Long = 0L,
    var watchedDurationInMilliseconds: Long = 0L
)