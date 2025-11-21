package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.active
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActivationStatusButton(
    isActive: Boolean,
    deactivationText: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isActive) Theme.colorScheme.background.bgSuccess
        else Theme.colorScheme.background.bgError

    val contentColor = if (isActive) Theme.colorScheme.success else Theme.colorScheme.error

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "statusBackgroundColor"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = contentColor,
        animationSpec = tween(durationMillis = 300),
        label = "statusDotColor"
    )

    val statusText = if (isActive) {
        stringResource(Res.string.active)
    } else {
        deactivationText
    }

    Row(
        modifier = modifier
            .wrapContentWidth()
            .background(
                color = animatedBackgroundColor,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color = animatedContentColor, shape = CircleShape)
        )
        Text(
            text = statusText,
            style = Theme.typography.label.medium,
            softWrap = false,
            color = animatedContentColor
        )
    }
}