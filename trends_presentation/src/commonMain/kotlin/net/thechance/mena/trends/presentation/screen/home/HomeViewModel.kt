package net.thechance.mena.trends.presentation.screen.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class HomeViewModel(
    @Provided private val repository: ReelsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<HomeScreenState, HomeUiEffect>(HomeScreenState()),
    HomeInteractionListener {

    init {
        getFeedReels()
    }

    fun addReelLike(reelId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(reelId) },
            block = { repository.addReelLike(reelId) },
            onError = { error ->
                updateLikesOnUi(reelId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
            onSuccess = { updatedReel -> updateReelInPagingData(reelId) { updatedReel.toUiState() } }
        )
    }

    private fun updateLikesOnUi(reelId: String) {
        updateReelInPagingData(reelId) { reel ->
            reel.copy(
                isLiked = !reel.isLiked,
                likesCount = if (reel.isLiked) reel.likesCount - 1 else reel.likesCount + 1
            )
        }
    }

    fun removeReelLike(reelId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(reelId) },
            block = { repository.removeReelLike(reelId) },
            onError = { error ->
                updateLikesOnUi(reelId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
        )
    }

    private fun updateReelInPagingData(reelId: String, transform: (ReelUiState) -> ReelUiState) {
        val currentData = state.value.reelsStateFlow.value
        val updatedData = currentData.map { reel ->
            if (reel.id == reelId) transform(reel) else reel
        }
        state.value.reelsStateFlow.value = updatedData
        updateState { copy(reels = state.value.reelsStateFlow) }
    }

    fun getFeedReels() {
        tryToCollectFlow(
            block = ::createPager,
            onStart = { updateState { copy(isLoading = true) } },
            onNewValue = { uiPagingData ->
                state.value.reelsStateFlow.value = uiPagingData
                updateState { copy(reels = reelsStateFlow, isLoading = false) }
            },
            onError = { error -> updateState { copy(error = error, isLoading = false) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun createPager(): Flow<PagingData<ReelUiState>> {
        return createPager(
            scope = viewModelScope,
            loadPage = { page -> repository.getFeedReels(page) }
        ).map { pagingData -> pagingData.map { it.toUiState() } }
    }

    override fun onClickAddReel() {
        sendEffect(HomeUiEffect.NavigateToAddReel)
    }

    override fun onClickEditTags() {
        sendEffect(HomeUiEffect.NavigateToChangeTags)
    }

    override fun onClickManageMyTrends() {
        sendEffect(HomeUiEffect.NavigateToManageMyTrends)
    }

    override fun onClickReel(reelId: String) {
        sendEffect(HomeUiEffect.NavigateToReelDetails(reelId))
    }

    override fun onClickLike(reelId: String, isLiked: Boolean) {
        if (isLiked) {
            removeReelLike(reelId)
        } else {
            addReelLike(reelId)
        }
    }

    override fun onClickRetry() {
        updateState { copy(error = null) }
        getFeedReels()
    }

    override fun onClickExpandDescription(reelId: String) {
        state.value.reelsStateFlow.value =
            state.value.reelsStateFlow.value.map { reel ->
                reel.takeIf { it.id != reelId }
                    ?: reel.copy(isDescriptionExpanded = !reel.isDescriptionExpanded)
            }
    }
}