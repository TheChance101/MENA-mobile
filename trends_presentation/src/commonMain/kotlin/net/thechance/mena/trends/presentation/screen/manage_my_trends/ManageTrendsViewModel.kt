package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided


@KoinViewModel
internal class ManageTrendsViewModel(
    @Provided private val trendsRepository: TrendsRepository,
    @Provided private val userRepository: UserRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageTrendsScreenState, ManageTrendsUiEffect>(ManageTrendsScreenState()),
    ManageTrendsInteractionListener {

    init {
        getCurrentUserInfo()
    }

    fun getTrends() {
        tryToExecute(
            block = {
                createPager(
                    scope = viewModelScope,
                    loadPage = { page -> trendsRepository.getAllCurrentUserTrends(page) }
                )
            },
            onSuccess = ::onGetTrendsSuccess,
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    fun getCurrentUserInfo() {
        tryToExecute(
            block = { userRepository.getUser() },
            onSuccess = ::onGetUserSuccess,
            onError = { updateState { copy(error = it) } },
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun onGetUserSuccess(flow: Flow<User?>) {
        flow.map { it?.toUiState() }
            .onEach { userUi ->
                userUi?.let {
                    updateState { copy(profile = it) }
                }
            }.launchIn(viewModelScope)
    }

    private fun onGetTrendsSuccess(trendsFlow: Flow<PagingData<Trend>>) {
        val uiTrendsFlow = trendsFlow
            .map { pagingData: PagingData<Trend> ->
            pagingData.map { trend -> trend.toUiState() }
        }
        updateState { copy(isLoading = false, trends = uiTrendsFlow) }
    }

    override fun onClickTrend(trend: String) {
        val trendSource = when (state.value.selectedTab) {
            SelectTab.MyTrends -> Route.TrendSource.MyTrends
            SelectTab.Favorites -> Route.TrendSource.Favorites
        }
        sendEffect(ManageTrendsUiEffect.NavigateToTrend(trend, trendSource))
    }

    override fun onClickBack() {
        sendEffect(ManageTrendsUiEffect.NavigateBack)
    }

    override fun onClickRetry() {
        updateState { copy(error = null) }

        getCurrentUserInfo()
        loadSelectedTabData(tab = state.value.selectedTab)
    }

    override fun onSelectTab(tab: SelectTab) {
        if (state.value.selectedTab != tab) {
            updateState { copy(selectedTab = tab) }
            loadSelectedTabData(tab)
        }
    }

    fun loadSelectedTabData(tab: SelectTab) {
        when (tab) {
            SelectTab.MyTrends -> getTrends()
            SelectTab.Favorites -> getFavoriteTrends()
        }
    }

    override fun onGetRefreshedThumbnail(trendId: String) {
        tryToExecute(
            block = { trendsRepository.getTrendUrls(trendId).thumbnailUrl },
            onSuccess = { refreshedUrl -> onGetRefreshedThumbnailSuccess(refreshedUrl, trendId) }
        )
    }

    private fun onGetRefreshedThumbnailSuccess(refreshedUrl: String, trendId: String) {
        updateState {
            copy(
                trends = state.value.trends.updateThumbnail(trendId, refreshedUrl),
                favoriteTrends = state.value.favoriteTrends.updateThumbnail(trendId, refreshedUrl)
            )
        }
    }

    private fun Flow<PagingData<TrendUiState>>.updateThumbnail(
        trendId: String,
        url: String
    ): Flow<PagingData<TrendUiState>> {
        return this.map { pagingData ->
            pagingData.map { trend ->
                trend.takeIf { it.id != trendId } ?: trend.copy(thumbnailUrl = url)
            }
        }
    }

    fun getFavoriteTrends() {
        tryToExecute(
            block = {
                createPager(
                    scope = viewModelScope,
                    loadPage = { page -> trendsRepository.getFavoriteTrends(page) }
                )
            },
            onSuccess = ::onGetFavoriteTrendsSuccess,
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun onGetFavoriteTrendsSuccess(flow: Flow<PagingData<Trend>>) {
        val uiTrendsFlow = flow.map { pagingData -> pagingData.map { it.toUiState() } }
        updateState { copy(favoriteTrends = uiTrendsFlow, isLoading = false) }
    }
}