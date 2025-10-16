package net.thechance.mena.core_chat.presentation.screen.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_big_rounded_shape
import mena.core_chat_presentation.generated.resources.ic_small_rounded_shape
import mena.core_chat_presentation.generated.resources.ic_subtract_back
import mena.core_chat_presentation.generated.resources.ic_subtract_front
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun ChatIcon(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(128.dp, 128.dp)
            .padding(top = 18.dp, bottom = 13.dp, end = 12.dp, start = 12.dp)
            .background(Theme.colorScheme.background.surface)
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_subtract_back),
            contentDescription = null,
            modifier = Modifier
                .width(78.dp)
                .height(74.dp)
                .then(
                    if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                        Modifier.align(Alignment.TopEnd)
                    } else {
                        Modifier.align(Alignment.TopStart)
                    }
                )
        )
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(78.dp)
                .then(
                    if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                        Modifier.align(Alignment.BottomStart)
                    } else {
                        Modifier.align(Alignment.BottomEnd)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(resource = Res.drawable.ic_subtract_front),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )

            Icon(
                painter = painterResource(resource = Res.drawable.ic_big_rounded_shape),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(20.dp)
            )

            Icon(
                painter = painterResource(resource = Res.drawable.ic_small_rounded_shape),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, bottom = 27.dp, start = 20.dp, end = 28.dp)
            )
        }
    }
}