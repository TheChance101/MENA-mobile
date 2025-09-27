package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
internal fun OtpInput(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = 6
) {
    BasicTextField(
        value = otpValue,
        modifier = modifier,
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
                    OTPCard(char)
                }
            }
        },
    )
}

@Composable
private fun OTPCard(char: String) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 66.dp)
            .background(
                color = Theme.colorScheme.primary.onPrimary,
                shape = RoundedCornerShape(size = Theme.radius.md)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = Theme.typography.headline.medium,
            color = Theme.colorScheme.shadePrimary
        )
    }
}