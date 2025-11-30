package net.thechance.mena.trends.presentation.screen.user_trend

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
import net.thechance.mena.trends.domain.model.TrendWatchSession
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.screen.user_trend.args.UserTrendArgs
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.base.PagingEvents
import net.thechance.mena.trends.presentation.shared.base.applyEvent
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UserTrendViewModel(
    @Provided private val userTrendArgs: UserTrendArgs,
    @Provided private val trendsRepository: TrendsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UserTrendState, UserTrendEffect>(UserTrendState()), UserTrendInteractionListener {

    init {
        updateState { copy(currentTrendId = userTrendArgs.trendId) }
        getFeedTrends()
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

    private fun createPager(): Flow<PagingData<UserTrendUiState>> {
        return createPager(
            scope = viewModelScope,
            loadPage = { page ->
                getTrendsBasedOnSource(
                    trendSource = userTrendArgs.trendSource,
                    page = page
                )
            }
        ).map { pagingData -> pagingData.map { it.toUserTrendUiState() } }
            .combine(state.value.pagingEvents) { pagingDate, pagingEvents ->
                pagingEvents.fold(pagingDate) { data, event ->
                    data.applyEvent(
                        event = event,
                        getItemId = { it.id }
                    )
                }
            }
    }

    private suspend fun getTrendsBasedOnSource(
        trendSource: Route.TrendSource,
        page: Int
    ): List<Trend> {
        return when (trendSource) {
            Route.TrendSource.Home -> trendsRepository.getFeedTrends(page, userTrendArgs.trendId)
            Route.TrendSource.MyTrends -> trendsRepository.getAllCurrentUserTrends(page, userTrendArgs.trendId)
            Route.TrendSource.Favorites -> trendsRepository.getFavoriteTrends(page, userTrendArgs.trendId)
        }
    }

    private fun addTrendLike(trendId: String) {
        tryToExecute(
            block = { trendsRepository.addTrendLike(trendId) },
            onError = { error ->
                toggleTrendLike(trendId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
        )
    }

    private fun removeTrendLike(trendId: String) {
        tryToExecute(
            block = { trendsRepository.removeTrendLike(trendId) },
            onError = { error ->
                toggleTrendLike(trendId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
        )
    }

    override fun onClickDescription(isCollapsed: Boolean) {
        updateState {
            copy(isDescriptionExpanded = !isCollapsed)
        }
    }

    override fun onClickPublisherInfo() {
        sendEffect(UserTrendEffect.NavigateToPublisherProfile)
    }

    override fun increaseTrendView(trendId: String) {
        if (state.value.isTrendDeleted == null) {
            tryToExecute(
                block = { trendsRepository.addTrendView(trendId) },
                dispatcher = defaultDispatcher
            )
        }
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
                    ).also(::sendTrendUpdates)
                }
            )
        )
    }

    private fun sendTrendUpdates(
        trend: UserTrendUiState,
        isDeleted: Boolean = false
    ) {
        tryToExecute(
            block = {
                trendsRepository.sendTrendUpdates(trend.toTrendUpdates(isDeleted = isDeleted))
            },
            dispatcher = defaultDispatcher
        )
    }

    override fun onGetRefreshVideoUrl(trendId: String) {
        tryToExecute(
            block = { trendsRepository.getTrendUrls(trendId).videoUrl },
            onSuccess = { refreshedUrl ->
                onGetRefreshVideoUrl(refreshedUrl, trendId)
            },
        )
    }

    override fun saveUserTrendEngagement(
        trendWatchSessionState: TrendWatchSessionState,
        trendId: String
    ) {
        tryToExecute(
            block = {
                trendsRepository.saveUserEngagementWithTrend(
                    prepareTrendData(
                        trendWatchSessionState,
                        trendId
                    )
                )
            },
            dispatcher = defaultDispatcher,
        )
    }

    private fun prepareTrendData(
        trendWatchSessionState: TrendWatchSessionState,
        trendId: String
    ): TrendWatchSession {
        val percentageOfVideoWatched =
            trendWatchSessionState.watchedDurationInMilliseconds.toFloat() / trendWatchSessionState.videoDurationInMilliseconds * 100
        trendWatchSessionState.trendId = trendId
        return trendWatchSessionState.toEntity(percentageOfVideoWatched)
    }

    override fun onClickRetry(trendId: String) {
        updateState { copy(currentTrendId = trendId, error = null) }
        onGetRefreshVideoUrl(trendId)
    }

    override fun onNetworkError() {
        updateState { copy(error = ErrorState.NoInternet) }
    }

    private fun onGetRefreshVideoUrl(refreshedUrl: String, trendId: String) {
        sendPagingEvent(
            event = PagingEvents.Update(
                itemId = trendId,
                transform = { trend ->
                    trend.copy(videoUrl = refreshedUrl)
                }
            )
        )
    }

    override fun onClickBack() {
        sendEffect(UserTrendEffect.NavigateBack)
    }

    override fun onChangeCurrentTrend(trendId: String) {
        updateState { copy(currentTrendId = trendId) }
    }

    override fun onClickDelete() {
        updateState {
            copy(isConfirmationDialogVisible = true)
        }
    }

    override fun onClickConfirmDelete() {
        tryToExecute(
            block = { trendsRepository.deleteTrendById(state.value.currentTrendId) },
            onSuccess = { onDeleteTrendSuccess() },
            onError = { errorState ->
                updateState {
                    copy(
                        error = errorState,
                        isTrendDeleted = false
                    )
                }
            },
            dispatcher = defaultDispatcher
        )
    }

    private fun onDeleteTrendSuccess() {
        updateState { copy(isConfirmationDialogVisible = false, isTrendDeleted = true) }
        sendPagingEvent(
            event = PagingEvents.Remove(
                itemId = state.value.currentTrendId,
                action = { trend -> sendTrendUpdates(trend = trend, isDeleted = true) }
            )
        )
    }

    override fun onDismissSuccessDialog() {
        updateState {
            copy(isTrendDeleted = null, isConfirmationDialogVisible = false)
        }
    }

    override fun onDismissConfirmationDialog() {
        updateState {
            copy(isConfirmationDialogVisible = false)
        }
    }

    override fun onDismissErrorDialog() {
        updateState {
            copy(isTrendDeleted = null, isConfirmationDialogVisible = false, error = null)
        }
    }

    private fun sendPagingEvent(event: PagingEvents<UserTrendUiState>) {
        state.value.pagingEvents.value = state.value.pagingEvents.value + event
    }
}
