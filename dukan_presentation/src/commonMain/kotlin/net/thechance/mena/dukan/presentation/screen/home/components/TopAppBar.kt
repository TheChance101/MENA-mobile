package net.thechance.mena.dukan.presentation.screen.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    onAddDukanButtonClicked: () -> Unit = {},
    isDukanPending: Boolean
) {
    AppBar(
        title = stringResource(resource = Res.string.Dukan),
        titleColor = Theme.colorScheme.shadePrimary,
        modifier = modifier,
        trailingContent = {
            Row {
                AnimatedContent(
                    targetState = isDukanPending,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = 100,
                                easing = EaseIn
                            )
                        ) togetherWith(fadeOut(
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = 100,
                                easing = EaseOut
                            ))
                        )
                    },
                    label = stringResource(resource = Res.string.dukan_button)
                )
                { isPending ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Theme.colorScheme.background.surfaceLow, shape = RoundedCornerShape(Theme.spacing._12))
                            .clip(shape = RoundedCornerShape(Theme.spacing._12))
                            .clickable(onClick = onAddDukanButtonClicked),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPending) {
                            Icon(
                                painter = painterResource(resource = Res.drawable.ic_dukan),
                                contentDescription = stringResource(resource = Res.string.dukan_icon)
                            )
                        } else {
                            Icon(
                                painter = painterResource(resource = Res.drawable.ic_add_dukan),
                                contentDescription = stringResource(resource = Res.string.add_dukan_icon)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun TopAppBarPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            TopAppBar(isDukanPending = true)
        }
    }
}