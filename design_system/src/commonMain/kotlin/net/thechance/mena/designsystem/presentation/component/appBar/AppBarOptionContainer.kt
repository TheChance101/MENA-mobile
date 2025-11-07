package net.thechance.mena.designsystem.presentation.component.appBar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_user
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppBarOptionContainer(
    modifier: Modifier = Modifier,
    isBadgeVisible: Boolean = false,
    badgeColor: Color = Theme.colorScheme.primary.primary,
    containerColor: Color = Theme.colorScheme.background.surfaceLow,
    shape: Shape = RoundedCornerShape(Theme.radius.md),
    badgeShape: Shape = RoundedCornerShape(Theme.radius.full),
    iconContentPadding: PaddingValues = PaddingValues(10.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val clickableModifier = onClick?.let {
        Modifier.clickable(
            onClick = it,
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
        )
    } ?: Modifier

    Box(
        modifier = modifier.size(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(containerColor, shape)
                .clip(shape)
                .then(clickableModifier)
                .padding(iconContentPadding)
        ) {
            content()
        }
        AnimatedVisibility(
            visible = isBadgeVisible,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-1).dp)
        ) {
            Box(
                Modifier
                    .size(5.dp)
                    .background(badgeColor, badgeShape)
            )
        }
    }
}

@Preview
@Composable
private fun IconContainerPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            AppBarOptionContainer(isBadgeVisible = true, onClick = {}, content = {
                Icon(
                    painter = painterResource(Res.drawable.ic_user),
                    contentDescription = null,
                )
            })
        }
    }
}