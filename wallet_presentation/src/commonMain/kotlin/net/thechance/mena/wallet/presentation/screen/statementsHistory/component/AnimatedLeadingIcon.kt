package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_cancel
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnimatedLeadingIcon(isEditMode: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                Theme.colorScheme.background.surfaceLow,
                RoundedCornerShape(Theme.radius.md)
            )
            .clip(RoundedCornerShape(Theme.radius.md)),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isEditMode,
            animationSpec = tween(durationMillis = 300)
        ) { editMode ->
            Icon(
                painter = painterResource(
                    if (editMode) Res.drawable.ic_cancel else Res.drawable.ic_arrow_left
                ),
                contentDescription = stringResource(Res.string.back_button),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}