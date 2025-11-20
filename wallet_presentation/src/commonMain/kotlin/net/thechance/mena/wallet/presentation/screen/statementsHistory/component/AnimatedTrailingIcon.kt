package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.edit
import mena.wallet_presentation.generated.resources.ic_edit
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AnimatedTrailingIcon(
    isEditMode: Boolean,
    hasStatements : Boolean,
    onEditClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = !isEditMode && !hasStatements,
        enter = ENTER_ANIMATION,
        exit = EXIT_ANIMATION
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_edit),
            contentDescription = stringResource(Res.string.edit),
            tint = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .size(40.dp)
                .background(
                    Theme.colorScheme.background.surfaceLow,
                    RoundedCornerShape(Theme.radius.md)
                )
                .clip(RoundedCornerShape(Theme.radius.md))
                .clickable { onEditClicked() }
                .padding(10.dp)
        )
    }
}

private val ANIMATION_DURATION = 300

private val ENTER_ANIMATION = fadeIn(tween(ANIMATION_DURATION)) +
    scaleIn(tween(ANIMATION_DURATION))

private val EXIT_ANIMATION = fadeOut(tween(ANIMATION_DURATION)) +
    scaleOut(tween(ANIMATION_DURATION))

@Preview
@Composable
private fun PreviewAnimatedTrailingIconNoStatementsNotEditMode() {
    MenaTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AnimatedTrailingIcon(
                isEditMode = false,
                hasStatements = false,
                onEditClicked = {}
            )
        }
    }
}