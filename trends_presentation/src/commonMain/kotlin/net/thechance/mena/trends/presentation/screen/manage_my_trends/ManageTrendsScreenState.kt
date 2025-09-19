package net.thechance.mena.trends.presentation.screen.manage_my_trends
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.StringResource


data class ManageTrendsScreenState(
    val reels: Flow<PagingData<ReelUiState>> = flowOf(),
    val userName: String = "",
    val profileImageUrl: String = "",
    val currentTab: String = "",
    val isLoading: Boolean = true,
    val errorMessage: StringResource? = null,
)
data class ReelUiState(
    val id: String,
    val thumbnailUrl: String,
)