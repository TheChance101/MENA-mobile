package net.thechance.mena.trends.presentation.video_player.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_pause
import mena.trends_presentation.generated.resources.pause_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PauseIcon(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Icon(
            painter = painterResource(Res.drawable.ic_pause),
            contentDescription = stringResource(Res.string.pause_icon)
        )
    }
}