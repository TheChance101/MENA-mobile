@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.edit
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_edit
import mena.wallet_presentation.generated.resources.statements
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.EmptyStatementsHistory
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.StatementsListContent
import net.thechance.mena.wallet.presentation.screen.wallet.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun StatementHistoryScreen(
    viewModel: StatementsHistoryViewModel = koinViewModel(),
    onNavigateBackClicked: () -> Unit,
    navigateToStatementDetails: (id: Uuid) -> Unit
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

    StatementHistoryContent(state = state, listener = viewModel)
}

@Composable
private fun StatementHistoryContent(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    WalletScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.statements),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = listener::onBackClicked,
                trailingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = stringResource(Res.string.edit),
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Theme.colorScheme.background.surfaceLow,
                                RoundedCornerShape(Theme.radius.md)
                            )
                            .clip(RoundedCornerShape(Theme.radius.md))
                            .clickable { listener.onEditClicked() }
                            .padding(10.dp)
                    )
                }
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = { listener.onRetryLoadStatementsHistoryClicked() }
    ) {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            state.statements.isEmpty() -> {
                EmptyStatementsHistory(modifier = Modifier.fillMaxSize())
            }

            else -> {
                StatementsListContent(
                    modifier = Modifier.fillMaxSize().padding(top = Theme.spacing._8),
                    listener = listener,
                    state = state,
                )
            }
        }
    }
}


private fun onStatementHistoryEffect(
    effect: StatementsHistoryEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToStatementDetails: (id: Uuid) -> Unit
) {
    when (effect) {
        StatementsHistoryEffect.NavigateBack -> onNavigateBackClicked()
        is StatementsHistoryEffect.NavigateToStatementDetails -> navigateToStatementDetails(effect.id)
    }
}