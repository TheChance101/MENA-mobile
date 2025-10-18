package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UserReelState, UserReelEffect>(UserReelState()), UserReelInteractionListener {

    init {
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
            dispatcher = ioDispatcher
        )
    }

    private fun createPager(): Flow<PagingData<UserReelUiState>> {
        return createPager(
            scope = viewModelScope,
            loadPage = { page -> reelsRepository.getFeedReels(page, userReelArgs.realId) }
        ).map { pagingData -> pagingData.map { it.toUserReelUiState() } }
    }

    override fun onDescriptionClick(isCollapsed: Boolean) {
        updateState {
            copy(isDescriptionExpanded = !isCollapsed)
        }
    }

    override fun onPublisherInfoClick() {
        sendEffect(UserReelEffect.NavigateToPublisherProfile)
    }

    override fun increaseReelView(reelId: String) {
        tryToExecute(
            block = { reelsRepository.addReelView(reelId) },
            onError = { error -> updateState { copy(error = error) } },
            dispatcher = ioDispatcher,
        )
    }

    override fun onLikeClick(reelId: String) {
        tryToExecute(
            onStart = { updateLikesOnUi(reelId) },
            block = { reelsRepository.toggleReelLike(reelId) },
            onError = { error ->
                onLikeClickFailed(reelId)
                updateState { copy(error = error) }
            },
            dispatcher = ioDispatcher,
            scope = viewModelScope,
            onSuccess = { updatedReel ->
                updateReelInPagingData(reelId) { updatedReel.toUserReelUiState() }
            }
        )
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

    override fun onBackClick() {
        sendEffect(UserReelEffect.NavigateBack)
    }

    override fun onDeleteClick() {
        updateState {
            copy(isConfirmationDialogVisible = true)
        }
    }

    override fun onConfirmDeleteClick() {
        tryToExecute(
            block = { reelsRepository.deleteReelById(userReelArgs.realId) },
            onSuccess = { onDeleteReelSuccess() },
            onError = { errorState -> updateState { copy(error = errorState) } },
            dispatcher = ioDispatcher
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