package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import org.jetbrains.compose.resources.StringResource


internal data class ManageTrendsScreenState(
    val isLoading: Boolean = true,
    val error: ErrorState? = null,
    val trends: Flow<PagingData<TrendUiState>> = flowOf(),
    val favoriteTrends: Flow<PagingData<TrendUiState>> = flowOf(),
    val profile: UserInfoUiState = UserInfoUiState(),
    val currentTab: String = "",
    val errorMessage: StringResource? = null,
    val selectedTab: SelectTab = SelectTab.MyTrends
)
internal data class TrendUiState(
    val id: String,
    val thumbnailUrl: String,
)
internal data class UserInfoUiState(
    val userName: String = "",
    val profileImageUrl: String = "",
)

enum class SelectTab { MyTrends, Favorites }