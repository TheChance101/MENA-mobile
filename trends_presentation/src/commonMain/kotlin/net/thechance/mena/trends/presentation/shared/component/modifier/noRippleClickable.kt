package net.thechance.mena.trends.presentation.shared.component.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed


internal fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }

    this.then(
        other = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
            enabled = enabled
        )
    )
}