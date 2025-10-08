package net.thechance.mena.wallet.presentation.screen.remove_statements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_cancel
import mena.wallet_presentation.generated.resources.ic_trush
import mena.wallet_presentation.generated.resources.remove_statements
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.StatementHistoryCard
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

private val pink = Color(0xFFFEE4E2)

@Composable
fun RemoveStatementsScreen(
    viewModel: RemoveStatementsViewModel = koinViewModel(),
    onCancelClicked: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onRemoveStatementsEffect(
                effect = effect,
                onCancelClicked = onCancelClicked
            )
        }
    )
    RemoveStatementsContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
fun RemoveStatementsContent(
    state: RemoveStatementsScreenState,
    interactionListener: RemoveStatementsInteractionListener
) {
    WalletScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.remove_statements),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_cancel),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = interactionListener::onCancelClicked,
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(7) {
                AnimatedStatementRow()
            }
        }
    }
}

@Composable
private fun AnimatedStatementRow() {
    var isVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = isVisible,
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatementHistoryCard(
                startDate = "2005",
                endDate = "2025",
                totalInflow = "7",
                totalOutflow = "1",
                onStatementCardClicked = {},
                modifier = Modifier.weight(1f)
            )


            Circle(
                onDeleteConfirmed = {
                    scope.launch {
                        delay(200)
                        isVisible = false
                    }
                }
            )
        }
    }
}

@Composable
private fun Circle(
    modifier: Modifier = Modifier,
    onDeleteConfirmed: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .height(40.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isExpanded = true
                scope.launch {
                    delay(400)
                    onDeleteConfirmed()
                }
            }
    ) {
        Box(
            modifier = if (isExpanded) {
                Modifier.fillMaxWidth().height(40.dp)
            } else {
                Modifier.size(40.dp)
            },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                if (isExpanded) {
                    drawRect(color = pink)
                } else {
                    drawCircle(color = pink)
                }
            }
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.ic_trush),
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

private fun onRemoveStatementsEffect(
    effect: RemoveStatementsEffect,
    onCancelClicked: () -> Unit,
) {
    when (effect) {
        RemoveStatementsEffect.navigateToStatements -> onCancelClicked()
    }
}

@Preview
@Composable
private fun RemoveStatementsScreenPreview() {
    MenaTheme {
        RemoveStatementsContent(
            state = RemoveStatementsScreenState(),
            interactionListener = object : RemoveStatementsInteractionListener {
                override fun onCancelClicked() {}
                override fun onDeleteClicked() {}
            }
        )
    }
}