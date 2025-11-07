package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.user_reel.args.UserReelArgs
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UserReelViewModel(
    @Provided private val userReelArgs: UserReelArgs,
    @Provided private val reelsRepository: ReelsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UserReelState, UserReelEffect>(UserReelState()), UserReelInteractionListener {

    init {
        updateState { copy(currentReelId = userReelArgs.realId) }
        getFeedReals()
    }

    private fun getFeedReals() {
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

    private fun createPager(): Flow<PagingData<UserReelUiState>> {
        return createPager(
            scope = viewModelScope,
            loadPage = { page ->
                getReelsBasedOnSource(
                    isFromHome = userReelArgs.isFromHome,
                    isFromManageTrends = userReelArgs.isFromManageTrends,
                    page = page
                )
            }
        ).map { pagingData -> pagingData.map { it.toUserReelUiState() } }
    }

    private suspend fun getReelsBasedOnSource(
        isFromHome: Boolean,
        isFromManageTrends: Boolean,
        page: Int
    ): List<Reel> {
        return when {
            isFromHome -> reelsRepository.getFeedReels(page, userReelArgs.realId)
            isFromManageTrends -> reelsRepository.getAllCurrentUserReels(page, userReelArgs.realId)
            else -> reelsRepository.getFeedReels(page, userReelArgs.realId)
        }
    }

    fun addReelLike(reelId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(reelId) },
            block = { reelsRepository.addReelLike(reelId) },
            onError = { error ->
                onLikeClickFailed(reelId)
                updateState { copy(error = error) }
            },
            dispatcher = defaultDispatcher,
            onSuccess = { updatedReel -> updateReelInPagingData(reelId) { updatedReel.toUserReelUiState() } }
        )
    }

    fun removeReelLike(reelId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(reelId) },
            block = { reelsRepository.removeReelLike(reelId) },
            onError = { error ->
                onLikeClickFailed(reelId)
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
        sendEffect(UserReelEffect.NavigateToPublisherProfile)
    }

    override fun increaseReelView(reelId: String) {
        if(state.value.isReelDeleted == null) {
            tryToExecute(
                block = { reelsRepository.addReelView(reelId) },
                dispatcher = defaultDispatcher
            )
        }
    }

    override fun onClickLike(reelId: String, isLiked: Boolean) {
        if (isLiked) {
            removeReelLike(reelId)
        } else {
            addReelLike(reelId)
        }
    }

    private fun onLikeClickFailed(reelId: String) {
        updateReelInPagingData(reelId) { reel ->
            reel.copy(
                isLiked = !reel.isLiked,
                likesCount = if (reel.isLiked) reel.likesCount - 1 else reel.likesCount + 1
            )
        }
    }

    private fun updateReelInPagingData(
        reelId: String,
        transform: (UserReelUiState) -> UserReelUiState
    ) {
        val currentData = state.value.reelsStateFlow.value
        val updatedData = currentData.map { reel ->
            if (reel.id == reelId) transform(reel) else reel
        }
        state.value.reelsStateFlow.value = updatedData
        updateState { copy(reels = reelsStateFlow) }
    }

    private fun updateLikesOnUi(reelId: String) {
        updateReelInPagingData(reelId) { reel ->
            reel.copy(
                isLiked = !reel.isLiked,
                likesCount = if (reel.isLiked) reel.likesCount - 1 else reel.likesCount + 1
            )
        }
    }

    override fun onClickBack() {
        sendEffect(UserReelEffect.NavigateBack)
    }

    override fun onChangeCurrentReel(reelId: String) {
        updateState { copy(currentReelId = reelId) }
    }

    override fun onClickDelete() {
        updateState {
            copy(isConfirmationDialogVisible = true)
        }
    }

    override fun onClickConfirmDelete() {
        tryToExecute(
            block = { reelsRepository.deleteReelById(state.value.currentReelId) },
            onSuccess = { onDeleteReelSuccess() },
            onError = { errorState -> updateState { copy(error = errorState) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun onDeleteReelSuccess() {
        updateState { copy(isConfirmationDialogVisible = false, isReelDeleted = true) }
    }

    override fun onDismissSuccessDialog() {
        updateState {
            copy(isReelDeleted = null, isConfirmationDialogVisible = false)
        }
    }

    override fun onDismissConfirmationDialog() {
        updateState {
            copy(isConfirmationDialogVisible = false)
        }
    }

    override fun onDismissErrorDialog() {
        updateState {
            copy(isReelDeleted = null, isConfirmationDialogVisible = false, error = null)
        }
    }
}