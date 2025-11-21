package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_delete
import mena.core_chat_presentation.generated.resources.ic_mic
import mena.core_chat_presentation.generated.resources.ic_send
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@Composable
fun RecordingBar(
    onSendClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSeconds = rememberRecordingTimer()
    val waveData = rememberWaveformData()
    val hasSent = remember { mutableStateOf(false) }
    Column(
        modifier = modifier.padding( Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy( Theme.spacing._8)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingRecordIndicator()

            Text(
                text = formatTime(totalSeconds),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary
            )

            WaveformAnimation(
                waveData = waveData,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(Res.drawable.ic_mic),
                contentDescription = null,
                tint = Theme.colorScheme.brand.brand,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DeleteButton(onCancelClick)

            SendButton(
                onSendClick = {
                    hasSent.value = true
                    onSendClick()
                },
                enabled = !hasSent.value
            )
        }
    }
}

@Composable
private fun PulsingRecordIndicator() {
    val startColor = Theme.colorScheme.error
    val endColor = Theme.colorScheme.background.bgError

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")

    val pulseColor by infiniteTransition.animateColor(
        initialValue = startColor,
        targetValue = endColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_color"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .background(
                color = pulseColor,
                shape = CircleShape
            )
    )
}


@Composable
private fun DeleteButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.size(52.dp, 48.dp).border(
            width = 1.dp, color = Theme.colorScheme.border
                .disabled,
            shape = RoundedCornerShape(Theme.radius.md)
        ).clip(RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_delete),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SendButton(
    onSendClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp, 48.dp)
            .background(
                color = Theme.colorScheme.primary.primary,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onSendClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_send),
            contentDescription = null,
            tint = Theme.colorScheme.primary.onPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun rememberRecordingTimer(): Long {
    var totalSeconds by rememberSaveable { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            totalSeconds++
        }
    }

    return totalSeconds
}

@Composable
private fun rememberWaveformData(): List<Float> {
    var waveData by rememberSaveable { mutableStateOf(listOf<Float>()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(100L)
            val newAmplitude = (Random.nextDouble() * 0.8 + 0.2).toFloat()
            waveData = waveData + newAmplitude
        }
    }

    return waveData
}

fun formatTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

@Preview(showBackground = true)
@Composable
fun RecordingBarPreview() {
    MenaTheme {
        RecordingBar(
            onSendClick = {},
            onCancelClick = {}
        )
    }
}