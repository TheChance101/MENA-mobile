package net.thechance.mena.trends.presentation.screen.user_trend

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendWatchSession
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.screen.user_trend.args.UserTrendArgs
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.ErrorState
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

    fun addTrendLike(trendId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(trendId) },
            block = { trendsRepository.addTrendLike(trendId) },
            onError = { error ->
                onLikeClickFailed(trendId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
            onSuccess = { updatedTrend -> updateTrendInPagingData(trendId) { updatedTrend.toUserTrendUiState() } }
        )
    }

    fun removeTrendLike(trendId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(trendId) },
            block = { trendsRepository.removeTrendLike(trendId) },
            onError = { error ->
                onLikeClickFailed(trendId)
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
        if (isLiked) {
            removeTrendLike(trendId)
        } else {
            addTrendLike(trendId)
        }
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
        state.value.trendsStateFlow.value =
            state.value.trendsStateFlow.value.map { trend ->
                trend.takeIf { it.id != trendId }
                    ?: trend.copy(videoUrl = refreshedUrl)
            }
    }

    private fun onLikeClickFailed(trendId: String) {
        updateTrendInPagingData(trendId) { trend ->
            trend.copy(
                isLiked = !trend.isLiked,
                likesCount = if (trend.isLiked) trend.likesCount - 1 else trend.likesCount + 1
            )
        }
    }

    private fun updateTrendInPagingData(
        trendId: String,
        transform: (UserTrendUiState) -> UserTrendUiState
    ) {
        val currentData = state.value.trendsStateFlow.value
        val updatedData = currentData.map { trend ->
            if (trend.id == trendId) transform(trend) else trend
        }
        state.value.trendsStateFlow.value = updatedData
        updateState { copy(trends = trendsStateFlow) }
    }

    private fun updateLikesOnUi(trendId: String) {
        updateTrendInPagingData(trendId) { trend ->
            trend.copy(
                isLiked = !trend.isLiked,
                likesCount = if (trend.isLiked) trend.likesCount - 1 else trend.likesCount + 1
            )
        }
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
}
