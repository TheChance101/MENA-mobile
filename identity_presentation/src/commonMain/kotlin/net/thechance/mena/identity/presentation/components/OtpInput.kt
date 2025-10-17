package net.thechance.mena.identity.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import sv.lib.squircleshape.SquircleShape

@Composable
internal fun OtpInput(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = 6
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = otpValue,
        modifier = modifier
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        onValueChange = { onOtpChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = { innerTextField ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(times = otpLength) { index ->
                    val char = otpValue.getOrNull(index)?.toString().orEmpty()
                    val showCursor = index == otpValue.length
                    OTPCard(char = char, showCursor = showCursor, isFocused = isFocused)
                }
            }
        },
    )
}

@Composable
private fun OTPCard(
    char: String,
    showCursor: Boolean = false,
    isFocused: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
    )

    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 66.dp)
            .background(
                color = Theme.colorScheme.primary.onPrimary,
                shape = SquircleShape( Theme.radius.md)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (char.isNotEmpty()) {
            Text(
                text = char,
                style = Theme.typography.headline.medium,
                color = Theme.colorScheme.shadePrimary
            )
        } else if (showCursor && isFocused) {
            Box(
                modifier = Modifier
                    .size(width = 1.5.dp, height = 28.dp)
                    .background(color = Theme.colorScheme.primary.primary.copy(alpha = cursorAlpha))
            )
        }
    }
}