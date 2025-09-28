package net.thechance.mena.trends.presentation.screen.user_reel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.user_reel.args.UserReelArgs
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UserReelViewModel(
    @Provided private val userReelArgs: UserReelArgs,
    @Provided private val reelsRepository: ReelsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UserReelState, UserReelEffect>(UserReelState()), UserReelInteractionListener {
    override fun onDescriptionClick(isCollapsed: Boolean) {
        updateState {
            copy(isDescriptionExpanded = !isCollapsed)
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