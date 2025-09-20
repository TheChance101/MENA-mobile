package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class UserReelViewModel(
    savedStateHandle: SavedStateHandle,
    @Provided private val reelsRepository: ReelsRepository
) : BaseViewModel<UserReelState, UserReelEffect>(UserReelState()), UserReelInteractionListener {

    val id = savedStateHandle.toRoute<Route.ReelDetails>().reelId

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
            block = { reelsRepository.deleteReelById(id) },
            onSuccess = { onDeleteReelSuccess() },
            onError = { errorState -> updateState { copy(error = errorState) } },
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