package net.thechance.mena.dukan.presentation.screen.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Dukan
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_dukan_icon
import mena.dukan_presentation.generated.resources.dukan_button
import mena.dukan_presentation.generated.resources.dukan_icon
import mena.dukan_presentation.generated.resources.ic_add_dukan
import mena.dukan_presentation.generated.resources.ic_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    onAddDukanIconClicked: () -> Unit,
    isUserHasDukan: Boolean,
) {
    AppBar(
        title = stringResource(resource = Res.string.Dukan),
        modifier = modifier,
        titleColor = Theme.colorScheme.shadePrimary,
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        trailingContent = {
            DukanIconButton(
                isDukanPending = isUserHasDukan,
                onAddDukanIconClicked = onAddDukanIconClicked
            )
        }
    )
}

@Composable
private fun DukanIconButton(
    isDukanPending: Boolean,
    onAddDukanIconClicked: () -> Unit,
) {
    AnimatedContent(
        targetState = isDukanPending,
        transitionSpec = { fadeTransitionSpec() },
        label = stringResource(resource = Res.string.dukan_button)
    )
    { isPending ->
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                    shape = RoundedCornerShape(Theme.radius.md)
                )
                .clip(shape = RoundedCornerShape(Theme.radius.md))
                .clickable(onClick = onAddDukanIconClicked),
            contentAlignment = Alignment.Center
        ) {
            if (isPending) {
                MenaIcon(
                    painter = painterResource(resource = Res.drawable.ic_dukan),
                    contentDescription = stringResource(resource = Res.string.dukan_icon)
                )
            } else {
                MenaIcon(
                    painter = painterResource(resource = Res.drawable.ic_add_dukan),
                    contentDescription = stringResource(resource = Res.string.add_dukan_icon)
                )
            }
        }
    }
}

private fun fadeTransitionSpec(): ContentTransform {
    return fadeIn(
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 100,
            easing = EaseIn
        )
    ) togetherWith fadeOut(
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 100,
            easing = EaseOut
        )
    )
}

@Preview
@Composable
private fun TopAppBarPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            TopAppBar(isUserHasDukan = true, onAddDukanIconClicked = {})
        }
    }
}