package net.thechance.mena.trends.presentation.screen.manage_my_trends
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import org.jetbrains.compose.resources.StringResource


internal data class ManageTrendsScreenState(
    val isLoading: Boolean = true,
    val error: ErrorState? = null,
    val reels: Flow<PagingData<ReelUiState>> = flowOf(),
    val userName: String = "Istanbul Squad",
    val profileImageUrl: String = "https://media.istockphoto.com/id/469738422/photo/large-boulders-on-lake-shore-at-sunset-minnesota-usa.jpg?s=612x612&w=0&k=20&c=4FzViDygZ8CgixTqt3VOudLJUP8uoSeh2UlD_qHYkAw=",
    val currentTab: String = "",
    val errorMessage: StringResource? = null,
)
data class ReelUiState(
    val id: String,
    val thumbnailUrl: String,
)