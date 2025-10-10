package net.thechance.mena.wallet.presentation.screen.remove_statements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
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
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.StatementHistoryCard
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun RemoveStatementsTestScreen(
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
    RemoveStatementsTestContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
fun RemoveStatementsTestContent(
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
            items(100) {
                AnimatedStatementRow()
            }
        }
    }
}

@Composable
private fun AnimatedStatementRow() {
    var isDeleting by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = isVisible,
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AnimatedStatementCard(
                isDeleting = isDeleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp)
            )

            AnimatedDeleteButton(
                isDeleting = isDeleting,
                onDeleteClick = {
                    isDeleting = true
                    scope.launch {
                        delay(300)
                        isVisible = false
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun AnimatedStatementCard(
    isDeleting: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        )
    )


    StatementHistoryCard(
        startDate = "2005",
        endDate = "2025",
        totalInflow = "7",
        totalOutflow = "1",
        onStatementCardClicked = {},
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0f, 0f)
            }
    )
}

@Composable
private fun AnimatedDeleteButton(
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidth = constraints.maxWidth.toFloat()

        val width by animateDpAsState(
            targetValue = if (isDeleting) maxWidth.dp else 48.dp,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        val height by animateDpAsState(
            targetValue = if (isDeleting) 0.dp else 48.dp,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        val cornerRadius by animateFloatAsState(
            targetValue = if (isDeleting) 20f else 100f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        val offsetY by animateDpAsState(
            targetValue = if (isDeleting) (-48).dp else 0.dp,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .offset(y = offsetY)
                .clickable(
                    enabled = !isDeleting,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDeleteClick()
                },
            contentAlignment = Alignment.Center
        ) {
            val color = Theme.colorScheme.background.bgError
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = color,
                    cornerRadius = CornerRadius(cornerRadius.dp.toPx())
                )
            }

            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(Res.drawable.ic_trush),
                contentDescription = null,
                tint = Theme.colorScheme.error
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
private fun RemoveStatementsTestScreenPreview() {
    MenaTheme {
        RemoveStatementsTestContent(
            state = RemoveStatementsScreenState(),
            interactionListener = object : RemoveStatementsInteractionListener {
                override fun onCancelClicked() {}
                override fun onDeleteClicked() {}
            }
        )
    }
}