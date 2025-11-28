package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun WaveformAnimation(
    waveData: List<Float>,
    modifier: Modifier = Modifier
) {
    val color = Theme.colorScheme.shadeTertiary

    val barWidth = 3.dp
    val spacing = 2.dp

    Canvas(
        modifier = modifier
            .padding(vertical = 8.dp)
            .height(28.dp)
            .clipToBounds(),
        onDraw = {
            val barWidthPx = barWidth.toPx()
            val spacingPx = spacing.toPx()
            val barAndSpacingPx = barWidthPx + spacingPx

            val totalWaveWidth = waveData.size * barAndSpacingPx

            val offsetX = if (totalWaveWidth > size.width) size.width - totalWaveWidth else 0f

            clipRect {
                translate(left = offsetX) {
                    waveData.forEachIndexed { index, amplitude ->

                        val x = index * barAndSpacingPx
                        val barHeight = amplitude * size.height
                        val topY = (size.height - barHeight) / 2f

                        drawLine(
                            color = color,
                            start = Offset(x, topY),
                            end = Offset(x, topY + barHeight),
                            strokeWidth = barWidthPx,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    )
}
@Composable
fun VoiceMessageWaveform(
    waveData: List<Float>,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val activeColor = Theme.colorScheme.primary.primary
    val inactiveColor = Theme.colorScheme.shadeTertiary

    Canvas(
        modifier = modifier
            .clipToBounds(),
        onDraw = {
            if (waveData.isEmpty()) return@Canvas

            val totalBarAndSpacingPx = size.width / waveData.size
            val barWidthPx = totalBarAndSpacingPx * 0.6f
            val spacingPx = totalBarAndSpacingPx * 0.4f

            val coercedProgress = progress.coerceIn(0f, 1f)

            val activeBarThreshold = waveData.size * coercedProgress

            waveData.forEachIndexed { index, amplitude ->
                val barColor = when {
                    index < activeBarThreshold.toInt() -> activeColor

                    index == activeBarThreshold.toInt() -> {
                        val partialProgress = activeBarThreshold - index
                        lerp(inactiveColor, activeColor, partialProgress)
                    }

                    else -> inactiveColor
                }

                val x = index * totalBarAndSpacingPx + (spacingPx / 2f)

                val barHeight = amplitude.coerceIn(0f, 1f) * size.height
                val topY = (size.height - barHeight) / 2f

                drawLine(
                    color = barColor,
                    start = Offset(x, topY),
                    end = Offset(x, topY + barHeight),
                    strokeWidth = barWidthPx,
                    cap = StrokeCap.Round
                )
            }
        }
    )
}