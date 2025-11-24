package net.thechance.mena.trends.presentation.screen.user_trend

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.util.TimeAgoValue

internal data class UserTrendState(
    val trends: Flow<PagingData<UserTrendUiState>> = flowOf(),
    val isLoading: Boolean = false,
    val currentTrendId: String = "",
    val error: ErrorState? = null,
    val isConfirmationDialogVisible: Boolean = false,
    val isTrendDeleted: Boolean? = null,
    val isDescriptionExpanded: Boolean = false,
    val trendsStateFlow: MutableStateFlow<PagingData<UserTrendUiState>> = MutableStateFlow(PagingData.empty())
)

internal data class UserTrendUiState(
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

data class TrendWatchSessionState(
    var trendId: String = "",
    var watchStartTime: LocalDateTime? = null,
    var watchEndTime: LocalDateTime? = null,
    var videoDurationInMilliseconds: Long = 0L,
    var watchedDurationInMilliseconds: Long = 0L
)