package net.thechance.mena.trends.presentation.screen.user_reel.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.confirmation_message
import mena.trends_presentation.generated.resources.delete
import mena.trends_presentation.generated.resources.delete_reel
import mena.trends_presentation.generated.resources.fail_delete_message
import mena.trends_presentation.generated.resources.fail_delete_title
import mena.trends_presentation.generated.resources.success_delete_message
import mena.trends_presentation.generated.resources.success_delete_title
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.trends.presentation.screen.user_reel.UserReelInteractionListener
import net.thechance.mena.trends.presentation.screen.user_reel.UserReelState
import net.thechance.mena.trends.presentation.screen.user_reel.shouldShowSuccessfulDeletionDialogue
import org.jetbrains.compose.resources.stringResource

fun ScaffoldScope.userReelScreenOverlays(
    state: UserReelState,
    listener: UserReelInteractionListener
) {

    dialog(isVisible = state.isConfirmationDialogVisible) {
        Dialog(
            title = stringResource(Res.string.delete_reel),
            message = stringResource(Res.string.confirmation_message),
            buttonText = stringResource(Res.string.delete),
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            isVisible = state.isConfirmationDialogVisible,
            onDismiss = { listener.onDismissConfirmationDialog() },
            onActionClick = { listener.onConfirmDeleteClick() },
            onCancelClick = { listener.onDismissConfirmationDialog() },
            dialogCornerShape = RoundedCornerShape(12.dp),
            cancelBackgroundShape = RoundedCornerShape(50),
            contentPadding = PaddingValues(16.dp)
        )
    }

    dialog(state.shouldShowSuccessfulDeletionDialogue()) {
        Dialog(
            title = stringResource(Res.string.success_delete_title),
            message = stringResource(Res.string.success_delete_message),
            buttonText = "",
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            isVisible = state.shouldShowSuccessfulDeletionDialogue(),
            onDismiss = {
                listener.onDismissSuccessDialog()
                listener.onBackClick()
            },
            onCancelClick = {
                listener.onDismissSuccessDialog()
                listener.onBackClick()
            },
            dialogCornerShape = RoundedCornerShape(12.dp),
            cancelBackgroundShape = RoundedCornerShape(50),
            contentPadding = PaddingValues(16.dp)
        )
    }

    dialog(state.error != null) {
        Dialog(
            title = stringResource(Res.string.fail_delete_title),
            message = stringResource(Res.string.fail_delete_message),
            buttonText = "",
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            isVisible = state.error != null,
            onDismiss = { listener.onDismissErrorDialog() },
            onCancelClick = { listener.onDismissErrorDialog() },
            dialogCornerShape = RoundedCornerShape(12.dp),
            cancelBackgroundShape = RoundedCornerShape(50),
            contentPadding = PaddingValues(16.dp)
        )
    }

}