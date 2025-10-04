package net.thechance.mena.wallet.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.downloading_started
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.CustomToastState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val ANIMATION_DURATION = 500

@Composable
fun CustomToast(
    toastState: CustomToastState,
    modifier: Modifier = Modifier
) {
    val toastBackgroundColor = Color(0xB2121212)

    AnimatedVisibility(
        visible = toastState.isVisible,
        enter =
            fadeIn(tween(ANIMATION_DURATION)) +
                    slideInVertically(
                        animationSpec = tween(ANIMATION_DURATION),
                        initialOffsetY = { -it / 2 },
                    ),
        exit =
            fadeOut(tween(ANIMATION_DURATION)) +
                    slideOutVertically(
                        animationSpec = tween(ANIMATION_DURATION),
                        targetOffsetY = { -it / 2 },
                    ),
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = toastState.messageRes?.let { stringResource(it) } ?: "",
            style = Theme.typography.body.small,
            color = Theme.colorScheme.primary.onPrimary,
            modifier = Modifier
                .background(
                    color = toastBackgroundColor,
                    shape = RoundedCornerShape(Theme.radius.md)
                )
                .padding(vertical = 12.dp, horizontal = 16.dp)
            ,
        )
    }
}


@Preview
@Composable
private fun CustomToastPreview() {
    MenaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Theme.colorScheme.primary.onPrimary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomToast(
                toastState = CustomToastState(
                    messageRes = Res.string.downloading_started,
                    isVisible = true
                )
            )
        }

    }

}