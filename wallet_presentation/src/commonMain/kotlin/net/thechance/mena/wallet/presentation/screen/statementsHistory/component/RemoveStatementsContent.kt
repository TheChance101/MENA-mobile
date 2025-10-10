package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_cancel
import mena.wallet_presentation.generated.resources.ic_trush
import mena.wallet_presentation.generated.resources.remove_statements
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RemoveStatementsContent(
    state: StatementsHistoryScreenState,
    interactionListener: StatementsHistoryInteractionListener
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
                onLeadingClick = interactionListener::onCancelEditClicked,
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) }
    ) {
        AnimatedStatementList(
            state = state,
            interactionListener = interactionListener
        )
    }
}


@Composable
private fun AnimatedStatementList(
    state: StatementsHistoryScreenState,
    interactionListener: StatementsHistoryInteractionListener,
) {
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
                    .padding(end = 48.dp),
                state = state,
                interactionListener = interactionListener
            )

            AnimatedDeleteButton(
                isDeleting = isDeleting,
                onDeleteClick = {
                    isDeleting = true
                    scope.launch {
                        delay(300)
                        isVisible = false
                        interactionListener.onDeleteClicked()
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
    modifier: Modifier = Modifier,
    state: StatementsHistoryScreenState,
    interactionListener: StatementsHistoryInteractionListener

) {
    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        )
    )

    StatementsListContent(
        listener = interactionListener,
        state = state,
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