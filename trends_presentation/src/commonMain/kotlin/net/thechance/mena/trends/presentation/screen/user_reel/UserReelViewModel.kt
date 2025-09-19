package net.thechance.mena.trends.presentation.screen.user_reel

import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class UserReelViewModel(
    @Provided private val reelsRepository: ReelsRepository
) : BaseViewModel<UserReelState, UserReelEffect>(UserReelState()), UserReelInteractionListener {

    private fun handleError(throwable: Throwable) {
        val errorRes = when (throwable) {
            //TODO() WILL HANDLE EXCEPTIONS
            else -> {}
        }
    }

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
            block = { reelsRepository.deleteReelById(state.value.id.orEmpty()) },
            onSuccess = { ::onDeleteReelSuccess },
            onError = { handleError(it) },
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
            copy(isReelDeleted = null, isConfirmationDialogVisible = false)
        }
    }
}