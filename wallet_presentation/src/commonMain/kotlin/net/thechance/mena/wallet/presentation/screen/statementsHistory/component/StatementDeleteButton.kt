package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_trush
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatementDeleteButton(
    isVisible: Boolean,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val fullWidth = constraints.maxWidth.dp
        val fullHeight = constraints.minHeight.dp

        val buttonWidth by animateDpAsState(
            targetValue = if (isDeleting) fullWidth else 48.dp,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        val buttonHeight by animateDpAsState(
            targetValue = if (isDeleting) fullHeight else 48.dp,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        Box(
            modifier = modifier
                .height(buttonHeight)
                .width(buttonWidth)
                .background(
                    color = Theme.colorScheme.background.bgError,
                    shape = if (isDeleting) RoundedCornerShape(8.dp) else CircleShape
                )
                .clickable(
                    enabled = isVisible,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDeleteClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.ic_trush),
                contentDescription = "Delete",
                tint = Theme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
private fun StatementDeleteButtonPreview() {
    StatementDeleteButton(
        isVisible = true,
        isDeleting = true,
        onDeleteClick = {}
    )
}