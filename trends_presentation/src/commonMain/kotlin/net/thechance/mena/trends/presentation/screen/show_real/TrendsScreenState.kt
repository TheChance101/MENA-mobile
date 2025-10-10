package net.thechance.mena.trends.presentation.screen.show_real

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import org.jetbrains.compose.resources.StringResource


data class TrendsScreenState(
    val isLoading: Boolean = true,
    val error: ErrorState? = null,
    val reels: Flow<PagingData<TrendUiState>> = flowOf(),
    val errorMessage: StringResource? = null,
) {
    data class TrendUiState(
        val id: String,
        val profileImageUrl: String = "",
        val thumbnailUrl: String = "",
        val userName: String = "",
        val timeAgo: LocalDateTime? = null,
        val videoUrl: String = "",
        val description: String = "",
        val likes: Int = 0,
        val views: Int = 0
    )
}
