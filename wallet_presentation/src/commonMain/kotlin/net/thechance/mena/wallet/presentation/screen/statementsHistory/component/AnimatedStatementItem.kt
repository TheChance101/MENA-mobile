@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun AnimatedStatementItem(
    statement: StatementsHistoryScreenState.StatementItem,
    isDividerVisible: Boolean,
    isEditMode: Boolean,
    cardOffsetX: Int,
    historyIconOffsetX: Int,
    deleteButtonOffsetX: Int,
    onDeleteClicked: () -> Unit,
    onStatementCardClicked: () -> Unit
) {

    val scale by animateFloatAsState(
        targetValue = if (statement.isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        )
    )

    val heightProgress by animateFloatAsState(
        targetValue = if (statement.isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 220,
            easing = LinearEasing
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animatedHeight(heightProgress)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            StatementDeleteButton(
                isDeleting = statement.isDeleting,
                onDeleteClick = onDeleteClicked,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset { IntOffset(deleteButtonOffsetX, 0) }
            )

            StatementHistoryCard(
                startDate = statement.startDate,
                endDate = statement.endDate,
                totalInflow = statement.totalInflow,
                totalOutflow = statement.totalOutflow,
                onStatementCardClicked = { if (!isEditMode) onStatementCardClicked() },
                isEditMode = isEditMode,
                historyIconOffsetX = historyIconOffsetX,
                modifier = Modifier
                    .offset { IntOffset(cardOffsetX, 0) }
                    .animatedScale(scale, 0f, 0.3f)
            )
        }

        if (isDividerVisible && !statement.isDeleting) {
            StatementDivider()
        }
    }
}

@Composable
private fun StatementDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Theme.colorScheme.stroke)
    )
}

private fun Modifier.animatedHeight(progress: Float) = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(
        width = placeable.width,
        height = (placeable.height * progress).toInt()
    ) {
        placeable.placeRelative(0, 0)
    }
}

private fun Modifier.animatedScale(scale: Float, pivotX: Float, pivotY: Float) =
    this.graphicsLayer {
        this.scaleX = scale
        this.scaleY = scale
        this.transformOrigin = TransformOrigin(pivotX, pivotY)
    }

@Preview
@Composable
private fun AnimatedStatementItemPreview() {
    MenaTheme {
        AnimatedStatementItem(
            statement = StatementsHistoryScreenState.StatementItem(
                id = Uuid.random(),
                startDate = "Jul 23 2025",
                endDate = "Aug 27 2025",
                totalInflow = "2000.0",
                totalOutflow = "4200.0",
                fileName = "",
                isDeleting = false
            ),
            isDividerVisible = true,
            cardOffsetX = 10,
            historyIconOffsetX = 10,
            deleteButtonOffsetX = 10,
            isEditMode = false,
            onDeleteClicked = {},
            onStatementCardClicked = {}
        )
    }
}