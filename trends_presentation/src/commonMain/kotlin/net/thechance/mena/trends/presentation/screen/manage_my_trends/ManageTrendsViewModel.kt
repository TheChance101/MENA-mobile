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
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided


@KoinViewModel
internal class ManageTrendsViewModel(
    @Provided private val reelsRepository: ReelsRepository,
    @Provided private val userRepository: UserRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageTrendsScreenState, ManageTrendsUiEffect>(ManageTrendsScreenState()),
    ManageTrendsInteractionListener {

    init {
        getCurrentUserInfo()
    }

    fun getReels() {
        tryToExecute(
            block = {
                createPager(
                    scope = viewModelScope,
                    loadPage = { page -> reelsRepository.getAllCurrentUserReels(page) }
                )
            },
            onSuccess = ::onGetReelsSuccess,
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

    private fun onGetReelsSuccess(reelsFlow: Flow<PagingData<Reel>>) {
        val uiReelsFlow = reelsFlow
            .map { pagingData: PagingData<Reel> ->
            pagingData.map { reel -> reel.toUiState() }
        }
        updateState { copy(isLoading = false, reels = uiReelsFlow) }
    }

    override fun onClickReel(reelId: String) {
        val reelSource = when (state.value.selectedTab) {
            SelectTab.MyTrends -> Route.ReelSource.MyTrends
            SelectTab.Favorites -> Route.ReelSource.Favorites
        }
        sendEffect(ManageTrendsUiEffect.NavigateToTrend(reelId, reelSource))
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
            SelectTab.MyTrends -> getReels()
            SelectTab.Favorites -> getFavoriteReels()
        }
    }

    override fun onGetRefreshedThumbnail(reelId: String) {
        tryToExecute(
            block = { reelsRepository.getReelUrls(reelId).thumbnailUrl },
            onSuccess = { refreshedUrl -> onGetRefreshedThumbnailSuccess(refreshedUrl, reelId) }
        )
    }

    private fun onGetRefreshedThumbnailSuccess(refreshedUrl: String, reelId: String) {
        updateState {
            copy(
                reels = state.value.reels.updateThumbnail(reelId, refreshedUrl),
                favoriteReels = state.value.favoriteReels.updateThumbnail(reelId, refreshedUrl)
            )
        }
    }

    private fun Flow<PagingData<ReelUiState>>.updateThumbnail(
        reelId: String,
        url: String
    ): Flow<PagingData<ReelUiState>> {
        return this.map { pagingData ->
            pagingData.map { reel ->
                reel.takeIf { it.id != reelId } ?: reel.copy(thumbnailUrl = url)
            }
        }
    }

    fun getFavoriteReels() {
        tryToExecute(
            block = {
                createPager(
                    scope = viewModelScope,
                    loadPage = { page -> reelsRepository.getFavoriteReels(page) }
                )
            },
            onSuccess = ::onGetFavoriteReelsSuccess,
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun onGetFavoriteReelsSuccess(flow: Flow<PagingData<Reel>>) {
        val uiReelsFlow = flow.map { pagingData -> pagingData.map { it.toUiState() } }
        updateState { copy(favoriteReels = uiReelsFlow, isLoading = false) }
    }
}