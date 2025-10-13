package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.remove_statements
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditModeContent(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    WalletScaffold(
        topBar = { EditModeAppBar(listener = listener) },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) }
    ) { StatementHistoryBody(state = state, listener = listener, isEditMode = true) }
}

@Composable
private fun EditModeAppBar(listener: StatementsHistoryInteractionListener) {
    AppBar(
        title = stringResource(Res.string.remove_statements),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = Theme.spacing._8),
        leadingContent = { AnimatedLeadingIcon(isEditMode = true) },
        onLeadingClick = { listener.onCancelEditClicked() },
        trailingContent = { AnimatedTrailingIcon(isEditMode = true, listener = listener) }
    )
}