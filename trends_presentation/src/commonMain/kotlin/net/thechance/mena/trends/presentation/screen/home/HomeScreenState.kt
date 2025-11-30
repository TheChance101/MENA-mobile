package net.thechance.mena.trends.presentation.screen.home

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.base.PagingEvents
import net.thechance.mena.trends.presentation.shared.util.TimeAgoValue
import org.jetbrains.compose.resources.StringResource


data class HomeScreenState(
    val isLoading: Boolean = true,
    val error: ErrorState? = null,
    val trends: Flow<PagingData<TrendUiState>> = flowOf(),
    val errorMessage: StringResource? = null,
    val pagingEvents: MutableStateFlow<List<PagingEvents<TrendUiState>>> = MutableStateFlow(emptyList())
)

data class TrendUiState(
    val id: String,
    val profileImageUrl: String = "",
    val thumbnailUrl: String = "",
    val userName: String = "",
    val timeAgo: TimeAgoValue? = null,
    val videoUrl: String = "",
    val description: String = "",
    val likesCount: Int = 0,
    val viewsCount: Int = 0,
    val isLiked: Boolean = false,
    val isDescriptionExpanded: Boolean = false
)