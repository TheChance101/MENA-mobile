package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.noHoverClickable(
    enabled: Boolean = true,
    onClick: (() -> Unit)
): Modifier {

    val interactionSource = remember { MutableInteractionSource() }

    return Modifier.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick,
        enabled = enabled
    )
}
