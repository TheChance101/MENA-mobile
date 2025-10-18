package net.thechance.mena.wallet.presentation.screen.statementsHistory

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.remove_statements
import mena.wallet_presentation.generated.resources.statements
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.AnimatedLeadingIcon
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.AnimatedTrailingIcon
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.StatementHistoryBody
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatementHistoryScreen(
    viewModel: StatementsHistoryViewModel = koinViewModel(),
    onNavigateBackClicked: () -> Unit,
    navigateToStatementDetails: (statementLocation: StorageLocation) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onStatementHistoryEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                navigateToStatementDetails = navigateToStatementDetails
            )
        }
    )

    StatementHistoryScreenContent(state = state, listener = viewModel)
}

@Composable
private fun StatementHistoryScreenContent(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    AnimatedContent(
        targetState = state.isEditMode,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(500))
        }
    ) { isEditMode ->
        if (isEditMode) {
            EditModeContent(state = state, listener = listener)
        } else {
            NormalModeContent(state = state, listener = listener)
        }
    }
}

@Composable
private fun EditModeContent(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    WalletScaffold(
        topBar = { EditModeAppBar(listener = listener) },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) }
    ) { StatementHistoryBody(state = state, listener = listener) }
}

@Composable
private fun EditModeAppBar(listener: StatementsHistoryInteractionListener) {
    AppBar(
        title = stringResource(Res.string.remove_statements),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = Theme.spacing._8),
        leadingContent = { AnimatedLeadingIcon(isEditMode = true) },
        onLeadingClick = { listener.onCancelEditModeClicked() },
        trailingContent = {
            AnimatedTrailingIcon(
                isEditMode = true,
                hasStatements = true,
                onEditClicked = { listener.onEditClicked() }
            )
        }
    )
}

@Composable
private fun NormalModeContent(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    WalletScaffold(
        topBar = {
            NormalModeAppBar(
                listener = listener,
                isStatementFound = state.statements.isEmpty()
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = { listener.onRetryLoadStatementsHistoryClicked() }
    ) { StatementHistoryBody(state = state, listener = listener) }
}

@Composable
private fun NormalModeAppBar(
    listener: StatementsHistoryInteractionListener,
    isStatementFound: Boolean
) {
    AppBar(
        title = stringResource(Res.string.statements),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = Theme.spacing._8),
        leadingContent = { AnimatedLeadingIcon(isEditMode = false) },
        onLeadingClick = { listener.onBackClicked() },
        trailingContent = {
            AnimatedTrailingIcon(
                isEditMode = false,
                hasStatements = isStatementFound,
                onEditClicked = { listener.onEditClicked() }
            )
        }
    )
}

private fun onStatementHistoryEffect(
    effect: StatementsHistoryEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToStatementDetails: (statementLocation: StorageLocation) -> Unit
) {
    when (effect) {
        StatementsHistoryEffect.NavigateBack -> onNavigateBackClicked()
        is StatementsHistoryEffect.NavigateToStatementDetails -> navigateToStatementDetails(effect.statementLocation)
    }
}