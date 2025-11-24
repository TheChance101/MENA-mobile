package net.thechance.mena.trends.presentation.screen.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class HomeViewModel(
    @Provided private val repository: TrendsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<HomeScreenState, HomeUiEffect>(HomeScreenState()),
    HomeInteractionListener {

    init {
        getFeedTrends()
    }

    fun addTrendLike(trendId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(trendId) },
            block = { repository.addTrendLike(trendId) },
            onError = { error ->
                updateLikesOnUi(trendId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
            onSuccess = { updatedTrend ->
                onAddLikeSuccess(trendId = trendId, updatedTrend = updatedTrend)
            }
        )
    }

    private fun onAddLikeSuccess(trendId: String, updatedTrend: Trend) {
        updateTrendInPagingData(trendId) {
            updatedTrend.toUiState().copy(isDescriptionExpanded = it.isDescriptionExpanded)
        }
    }

    private fun updateLikesOnUi(trendId: String) {
        updateTrendInPagingData(trendId) { trend ->
            trend.copy(
                isLiked = !trend.isLiked,
                likesCount = if (trend.isLiked) trend.likesCount - 1 else trend.likesCount + 1
            )
        }
    }

    fun removeTrendLike(trendId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(trendId) },
            block = { repository.removeTrendLike(trendId) },
            onError = { error ->
                updateLikesOnUi(trendId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
        )
    }

    private fun updateTrendInPagingData(trendId: String, transform: (TrendUiState) -> TrendUiState) {
        val currentData = state.value.trendsStateFlow.value
        val updatedData = currentData.map { trend ->
            if (trend.id == trendId) transform(trend) else trend
        }
        state.value.trendsStateFlow.value = updatedData
        updateState { copy(trends = state.value.trendsStateFlow) }
    }

    fun getFeedTrends() {
        tryToCollectFlow(
            block = ::createPager,
            onStart = { updateState { copy(isLoading = true) } },
            onNewValue = { uiPagingData ->
                state.value.trendsStateFlow.value = uiPagingData
                updateState { copy(trends = trendsStateFlow, isLoading = false) }
            },
            onError = { error -> updateState { copy(error = error, isLoading = false) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun createPager(): Flow<PagingData<TrendUiState>> {
        return createPager(
            scope = viewModelScope,
            loadPage = { page -> repository.getFeedTrends(page) }
        ).map { pagingData -> pagingData.map { it.toUiState() } }
    }

    override fun onClickAddTrend() {
        sendEffect(HomeUiEffect.NavigateToAddTrend)
    }

    override fun onClickEditTags() {
        sendEffect(HomeUiEffect.NavigateToChangeTags)
    }

    override fun onClickManageMyTrends() {
        sendEffect(HomeUiEffect.NavigateToManageMyTrends)
    }

    override fun onClickTrend(trendId: String) {
        sendEffect(HomeUiEffect.NavigateToTrendDetails(trendId))
    }

    override fun onClickLike(trendId: String, isLiked: Boolean) {
        if (isLiked) {
            removeTrendLike(trendId)
        } else {
            addTrendLike(trendId)
        }
    }

    override fun onClickRetry() {
        updateState { copy(error = null) }
        getFeedTrends()
    }

    override fun onClickExpandDescription(trendId: String) {
        state.value.trendsStateFlow.value =
            state.value.trendsStateFlow.value.map { trend ->
                trend.takeIf { it.id != trendId }
                    ?: trend.copy(isDescriptionExpanded = !trend.isDescriptionExpanded)
            }
    }

    override fun onGetRefreshedThumbnail(trendId: String) {
        tryToExecute(
            block = { repository.getTrendUrls(trendId).thumbnailUrl },
            onSuccess = { refreshedUrl ->
                onGetRefreshedThumbnailSuccess(refreshedUrl, trendId)
            },
        )
    }

    private fun onGetRefreshedThumbnailSuccess(refreshedUrl: String, trendId: String) {
        state.value.trendsStateFlow.value =
            state.value.trendsStateFlow.value.map { trend ->
                trend.takeIf { it.id != trendId }
                    ?: trend.copy(thumbnailUrl = refreshedUrl)
            }
    }
}