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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UserReelState, UserReelEffect>(UserReelState()), UserReelInteractionListener {

    init {
        getFeedReals()
    }

    private fun getFeedReals() {
        tryToExecute(
            block = {
                createPager(
                    scope = viewModelScope,
                    loadPage = { page -> reelsRepository.getFeedReels(page, userReelArgs.realId) }
                )
            },
            onSuccess = ::onGetReelsSuccess,
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = ioDispatcher,
            onError = { error -> updateState { copy(error = error) } }
        )
    }

    private fun onGetReelsSuccess(reelsFlow: Flow<PagingData<Reel>>) {
        val uiReelsFlow = reelsFlow.map { pagingData: PagingData<Reel> ->
            pagingData.map { reel -> reel.toUserReelUiState() }
        }
        updateState { copy(reels = uiReelsFlow) }
    }

    override fun onDescriptionClick(isCollapsed: Boolean) {
        updateState {
            copy(isDescriptionExpanded = !isCollapsed)
        }
    }

    override fun onPublisherInfoClick() {
        sendEffect(UserReelEffect.NavigateToPublisherProfile)
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