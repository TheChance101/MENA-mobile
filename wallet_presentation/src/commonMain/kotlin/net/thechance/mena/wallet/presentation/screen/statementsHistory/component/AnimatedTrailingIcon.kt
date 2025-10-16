package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnimatedTrailingIcon(
    isEditMode: Boolean,
    hasStatements : Boolean,
    onEditClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = !isEditMode && !hasStatements,
        enter = fadeIn(tween(300)) +
                scaleIn(tween(300)),
        exit = fadeOut(tween(300)) +
                scaleOut(tween(300))
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_edit),
            contentDescription = stringResource(Res.string.edit),
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