package net.thechance.mena.trends.presentation.shared.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> {
    var job: Job = Job().apply { complete() }
    return onCompletion { job.cancel() }.run {
        flow {
            coroutineScope {
                collect { value ->
                    if (!job.isActive) {
                        emit(value)
                        job = launch { delay(windowDuration) }
                    }
                }
            }
        }
    }
}

fun Modifier.gradientShadow(
    startColor: Color = Color(color = 0x00000033),
    endColor: Color = Color(color =  0x00000000),
    spread: Dp = 18.dp,
    offsetY: Dp = 2.dp
): Modifier = this.then(
    other = Modifier.drawBehind {
        val spreadPx = spread.toPx()
        val offsetYPx = offsetY.toPx()

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(startColor, endColor),
                startY = 0f,
                endY = size.height + spreadPx
            ),
            topLeft = Offset(0f, offsetYPx),
            size = Size(size.width, size.height + spreadPx)
        )
    }
)