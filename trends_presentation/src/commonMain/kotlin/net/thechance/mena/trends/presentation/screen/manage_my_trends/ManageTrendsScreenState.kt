package net.thechance.mena.trends.presentation.screen.manage_my_trends
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.StringResource


internal data class ManageTrendsScreenState(
    val isLoading: Boolean = true,
    val error: ManageTrendsErrorState? = null,
    val reels: Flow<PagingData<ReelUiState>> = flowOf(),
    val profile: UserInfoUiState = UserInfoUiState(),
    val currentTab: String = "",
    val errorMessage: StringResource? = null,
)
internal data class ReelUiState(
    val id: String,
    val thumbnailUrl: String,
)
internal data class UserInfoUiState(
    val userName: String = "",
    val profileImageUrl: String = "",
)

internal sealed class ManageTrendsErrorState {
    object NoInternet : ManageTrendsErrorState()
    data class RequestFailed(val message: String? = "Request failed") : ManageTrendsErrorState()
}