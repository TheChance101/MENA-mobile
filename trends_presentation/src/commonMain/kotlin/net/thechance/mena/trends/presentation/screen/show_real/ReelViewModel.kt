package net.thechance.mena.trends.presentation.screen.show_real

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class ReelViewModel(
    @Provided private val repository: ReelsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ReelScreenState, ReelUiEffect>(ReelScreenState()),
    ReelInteractionListener {

    init {
        getFeedReels()
    }

    fun toggleReelLike(reelId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(reelId) },
            block = { repository.toggleReelLike(reelId) },
            onError = { error ->
                updateLikesOnUi(reelId)
                updateState { copy(error = error) }
            },
            dispatcher = ioDispatcher,
            scope = viewModelScope,
            onSuccess = { updatedReel ->
                updateReelInPagingData(reelId) { updatedReel.toUiState() }
            }
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
            dispatcher = ioDispatcher
        )
    }

    private fun createPager(): Flow<PagingData<ReelUiState>> {
        return createPager(
            scope = viewModelScope,
            loadPage = { page -> repository.getFeedReels(page) }
        ).map { pagingData -> pagingData.map { it.toUiState() } }
    }

    override fun onAddReelClick() {
        sendEffect(ReelUiEffect.NavigateToAddReel)
    }

    override fun onEditTagsClick() {
        sendEffect(ReelUiEffect.NavigateToChangeTags)
    }

    override fun onManageMyTrendsClick() {
        sendEffect(ReelUiEffect.NavigateToManageMyTrends)
    }

    override fun onReelClick(reelId: String) {
        sendEffect(ReelUiEffect.NavigateToReelDetails(reelId))
    }

    override fun onLikeClick(reelId: String) {
        toggleReelLike(reelId)
    }
}