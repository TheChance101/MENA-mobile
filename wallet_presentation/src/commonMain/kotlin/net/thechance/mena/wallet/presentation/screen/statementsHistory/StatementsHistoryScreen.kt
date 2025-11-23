package net.thechance.mena.wallet.presentation.screen.statementsHistory

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.remove_statements
import mena.wallet_presentation.generated.resources.statements
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.navigation.StatementDetailsScreenRoute
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.AnimatedLeadingIcon
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.AnimatedTrailingIcon
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.StatementHistoryBody
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatementHistoryScreen(viewModel: StatementsHistoryViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onStatementHistoryEffect(effect = effect, navController = navController)
        }
    )

    StatementHistoryScreenContent(state = state, listener = viewModel)
}

@Composable
private fun StatementHistoryScreenContent(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    WalletScaffold(
        topBar = {
            AnimatedAppBar(state = state, listener = listener)
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        isLoading = state.isLoading,
        onRetry = { listener.onRetryLoadStatementsHistoryClicked() }
    ) { StatementHistoryBody(state = state, listener = listener) }
}

@Composable
private fun AnimatedAppBar(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    AnimatedContent(
        targetState = state.isEditMode,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith
                    fadeOut(animationSpec = tween(500))
        }
    ) { isEditMode ->
        if (isEditMode) {
            EditModeAppBar(listener = listener)
        } else {
            NormalModeAppBar(
                listener = listener,
                isStatementFound = state.statements.isEmpty()
            )
        }
    }
}

@Composable
private fun EditModeAppBar(listener: StatementsHistoryInteractionListener) {
    AppBar(
        title = stringResource(Res.string.remove_statements),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
private fun NormalModeAppBar(
    listener: StatementsHistoryInteractionListener,
    isStatementFound: Boolean
) {
    AppBar(
        title = stringResource(Res.string.statements),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
    navController: NavController
) {
    when (effect) {
        StatementsHistoryEffect.NavigateBack -> navController.popBackStack()
        is StatementsHistoryEffect.NavigateToStatementDetails -> {
            navController.navigate(
                StatementDetailsScreenRoute(
                    effect.statementLocation
                )
            )
        }
    }
}