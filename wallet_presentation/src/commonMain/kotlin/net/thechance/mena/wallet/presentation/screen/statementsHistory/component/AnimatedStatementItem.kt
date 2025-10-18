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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AnimatedStatementItem(
    statement: StatementsHistoryScreenState.StatementItem,
    isDividerVisible: Boolean,
    isEditMode: Boolean,
    cardOffsetX: Int,
    historyIconOffsetX: Int,
    deleteButtonOffsetX: Int,
    onDeleteClicked: (onDeleteComplete: (isSuccess: Boolean) -> Unit) -> Unit,
    onStatementCardClicked: (onViewStatementAvailable: (isPdfFound: Boolean) -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isDeleting by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        )
    )

    val heightProgress by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 220,
            easing = LinearEasing
        ),
        finishedListener = {
            if (isDeleting && it == 0f) {
                onDeleteClicked { isSuccess ->
                    scope.launch {
                        if (!isSuccess) {
                            isDeleting = false
                        }
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(
                    width = placeable.width,
                    height = (placeable.height * heightProgress).toInt()
                ) {
                    placeable.placeRelative(0, 0)
                }
            }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            StatementDeleteButton(
                isDeleting = isDeleting,
                onDeleteClick = {
                    isDeleting = true
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset { IntOffset(deleteButtonOffsetX, 0) }
            )

            StatementHistoryCard(
                startDate = statement.startDate,
                endDate = statement.endDate,
                totalInflow = statement.totalInflow.toString(),
                totalOutflow = statement.totalOutflow.toString(),
                onStatementCardClicked = {
                    if (!isEditMode) {
                        onStatementCardClicked { isPdfFound ->
                            scope.launch {
                                if (!isPdfFound) {
                                    isDeleting = true
                                }
                            }
                        }
                    }
                },
                isEditMode = isEditMode,
                historyIconOffsetX = historyIconOffsetX,
                modifier = Modifier
                    .offset { IntOffset(cardOffsetX, 0) }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0.3f)
                    }
            )
        }
        if (isDividerVisible && !isDeleting) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Theme.colorScheme.stroke)
            )
        }
    }
}

@Preview
@Composable
private fun AnimatedStatementItemPreview() {
    AnimatedStatementItem(
        statement = StatementsHistoryScreenState.StatementItem(
            id = 123,
            startDate = "Jul 23 2025",
            endDate = "Aug 27 2025",
            totalInflow = 2000.0,
            totalOutflow = 4200.0,
            fileName = ""
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