package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
fun Modifier.noHoverClickable(
    onClick: (() -> Unit)? = null
): Modifier {

    val enabled = onClick != null
    val interactionSource = remember { MutableInteractionSource() }

    return this.then(
        other = if (enabled) {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )

        } else {
            Modifier
        }
    )

}