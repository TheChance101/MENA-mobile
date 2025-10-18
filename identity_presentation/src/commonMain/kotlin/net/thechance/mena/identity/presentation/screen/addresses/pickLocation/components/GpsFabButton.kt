package net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_gps
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun GpsFabButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        containerColor = Theme.colorScheme.primary.primary,
        contentColor = Theme.colorScheme.primary.onPrimary,
        shape = RoundedCornerShape(Theme.radius.md),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 14.dp),
        modifier = modifier,
    ) { contentColor ->
        Crossfade(isLoading) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ic_gps),
                    tint = contentColor,
                    contentDescription = "GPS",
                    modifier = Modifier.size(20.dp)
                )
                GpsDotLoadingAnimation(isLoading)
            }
        }
    }
}

@Composable
private fun GpsDotLoadingAnimation(
    isLoading: Boolean
) {
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition()
        val animatedAlpha = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .size(7.dp)
                .alpha(animatedAlpha.value)
                .clip(RoundedCornerShape(100.dp))
                .background(Theme.colorScheme.primary.primary)
        )
    }
}