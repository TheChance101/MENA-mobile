package net.thechance.mena.trends.presentation.screen.show_real

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.trends.domain.entity.Reel
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

    private fun getFeedReels() {
        tryToExecute(
            block = {
                createPager(
                    scope = viewModelScope,
                    loadPage = { page -> repository.getFeedReels(page) }
                )
            },
            onSuccess = ::onReelLoaded,
            onError = { error -> updateState { copy(error = error) } },
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = ioDispatcher
        )
    }

    private fun onReelLoaded(reelsFlow: Flow<PagingData<Reel>>) {
        val uiReelsFlow = reelsFlow.map { pagingData: PagingData<Reel> ->
            pagingData.map { reel -> reel.toUiState() }
        }
        updateState { copy(isLoading = false, reels = uiReelsFlow) }
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
        state.value.reels?.let { reelsFlow ->
            val updatedFlow = reelsFlow.map { pagingData ->
                pagingData.map { uiState ->
                    if (uiState.id == reelId) uiState.copy(likes = uiState.likes + 1) else uiState
                }
            }
            updateState { copy(reels = updatedFlow) }
        }
        //TODO will handle it with like Api
    }
}