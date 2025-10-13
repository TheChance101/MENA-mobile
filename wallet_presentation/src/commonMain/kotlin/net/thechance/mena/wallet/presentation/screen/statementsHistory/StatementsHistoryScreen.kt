package net.thechance.mena.wallet.presentation.screen.statementsHistory

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.EditModeContent
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.NormalModeContent
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
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

    AnimatedContent(
        targetState = state.isEditMode,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(500))
        }
    ) { isEditMode ->
        if (isEditMode) { EditModeContent(state = state, listener = viewModel) }
        else { NormalModeContent(state = state, listener = viewModel) }
    }
}

@OptIn(ExperimentalUuidApi::class)
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