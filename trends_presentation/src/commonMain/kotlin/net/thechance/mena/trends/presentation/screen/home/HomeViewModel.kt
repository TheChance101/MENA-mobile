package net.thechance.mena.trends.presentation.screen.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendUpdates
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.screen.user_trend.UserTrendUiState
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.PagingEvents
import net.thechance.mena.trends.presentation.shared.base.applyEvent
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
        observeTrendsUpdates()
    }

    private fun observeTrendsUpdates() {
        tryToCollectFlow(
            block = repository::observeTrendUpdates,
            onNewValue = ::onTrendUpdates,
            onError = {},
            dispatcher = defaultDispatcher
        )
    }

    private fun onTrendUpdates(trendUpdates: TrendUpdates) {
        if(trendUpdates.isDeleted) {
            sendPagingEvent(PagingEvents.Remove(trendUpdates.trendId))
            return
        }
        sendPagingEvent(
            PagingEvents.Update(
                itemId = trendUpdates.trendId,
                transform = { trend ->
                    trend.copy(
                        isLiked = trendUpdates.isLiked,
                        likesCount = trendUpdates.likesCount,
                        viewsCount = trendUpdates.viewsCount
                    )
                }
            )
        )
    }

    private fun addTrendLike(trendId: String) {
        tryToExecute(
            block = { repository.addTrendLike(trendId) },
            onError = { error ->
                toggleTrendLike(trendId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
        )
    }

    private fun removeTrendLike(trendId: String) {
        tryToExecute(
            block = { repository.removeTrendLike(trendId) },
            onError = { error ->
                toggleTrendLike(trendId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
        )
    }

    private fun getFeedTrends() {
        tryToExecute(
            block = ::createPager,
            onStart = { updateState { copy(isLoading = true) } },
            onSuccess = { trends -> updateState { copy(trends = trends) } },
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
            .combine(state.value.pagingEvents) { pagingDate, pagingEvents ->
                pagingEvents.fold(pagingDate) { data, event ->
                    data.applyEvent(
                        event = event,
                        getItemId = { it.id }
                    )
                }
            }
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
        toggleTrendLike(trendId)
        if (isLiked) {
            removeTrendLike(trendId)
        } else {
            addTrendLike(trendId)
        }
    }

    private fun toggleTrendLike(trendId: String) {
        sendPagingEvent(
            event = PagingEvents.Update(
                itemId = trendId,
                transform = { trend ->
                    trend.copy(
                        isLiked = !trend.isLiked,
                        likesCount = if(trend.isLiked) trend.likesCount-1 else trend.likesCount+1
                    )
                }
            )
        )
    }

    override fun onClickRetry() {
        updateState { copy(error = null) }
        getFeedTrends()
    }

    override fun onClickExpandDescription(trendId: String) {
        sendPagingEvent(
            PagingEvents.Update(
                itemId = trendId,
                transform = { trend ->
                    trend.copy(isDescriptionExpanded = !trend.isDescriptionExpanded)
                }
            )
        )
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
        sendPagingEvent(
            PagingEvents.Update(
                itemId = trendId,
                transform = { trend -> trend.copy(thumbnailUrl = refreshedUrl) }
            )
        )
    }

    private fun sendPagingEvent(event: PagingEvents<TrendUiState>) {
        state.value.pagingEvents.value = state.value.pagingEvents.value + event
    }
}