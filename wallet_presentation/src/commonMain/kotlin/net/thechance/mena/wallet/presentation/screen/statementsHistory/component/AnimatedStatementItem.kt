package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun AnimatedStatementItem(
    statement: StatementsHistoryScreenState.StatementItem,
    lastStatement: StatementsHistoryScreenState.StatementItem,
    isEditMode: Boolean,
    offsetX: Int,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    var isDeleting by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        )
    )

    AnimatedVisibility(
        visible = isVisible,
        exit = shrinkVertically(animationSpec = tween(durationMillis = 400, easing = LinearEasing))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .offset { IntOffset(offsetX, 0) },
                contentAlignment = Alignment.Center
            ) {
                StatementDeleteButton(
                    isVisible = isEditMode,
                    isDeleting = isDeleting,
                    onDeleteClick = {
                        isDeleting = true
                        onDelete
                        scope.launch {
                            delay(450)
                            isVisible = false
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(60.dp)
                )
                StatementHistoryCard(
                    startDate = statement.startDate,
                    endDate = statement.endDate,
                    totalInflow = statement.totalInflow.toString(),
                    totalOutflow = statement.totalOutflow.toString(),
                    onStatementCardClicked = onClick,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0.3f)
                    },
                )
            }
            if (lastStatement != statement) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Theme.colorScheme.stroke)
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun AnimatedStatementItemPreview() {
    AnimatedStatementItem(
        statement = StatementsHistoryScreenState.StatementItem(
            id = Uuid.random(),
            startDate = "Jul 23 2025",
            endDate = "Aug 27 2025",
            totalInflow = 2000.0,
            totalOutflow = 4200.0
        ),
        lastStatement = StatementsHistoryScreenState.StatementItem(
            id = Uuid.random(),
            startDate = "Jul 23 2025",
            endDate = "Aug 27 2025",
            totalInflow = 2000.0,
            totalOutflow = 4200.0
        ),
        offsetX = 10,
        isEditMode = false,
        onDelete = {},
        onClick = {}
    )
}