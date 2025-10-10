package net.thechance.mena.core_chat.presentation.screen.syncContacts.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_phone
import mena.core_chat_presentation.generated.resources.ic_phone_back
import mena.core_chat_presentation.generated.resources.ic_phone_front
import mena.core_chat_presentation.generated.resources.ic_shadow_ball
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun PhoneIcon(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(128.dp, 122.dp)
            .padding(top = 18.dp, bottom = 7.dp, end = 11.dp, start = 10.dp)
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_phone_back),
            contentDescription = null,
            modifier = Modifier
                .width(80.dp)
                .height(78.dp)
                .then(
                    if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                        Modifier.align (Alignment.TopEnd)
                    } else {
                        Modifier.align (Alignment.TopStart)
                    }
                )
        )
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(78.dp)
                .then(
                    if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                        Modifier.align (Alignment.BottomStart)
                    } else {
                        Modifier.align (Alignment.BottomEnd)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_shadow_ball),
                contentDescription = null,
                modifier = Modifier
                    .width(28.dp)
                    .height(24.dp)
                    .align(Alignment.BottomCenter)
                    .blur(radius = 40.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
                tint = Theme.colorScheme.primary.primary
            )
            Image(
                painter = painterResource(resource = Res.drawable.ic_phone_front),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
            Icon(
                painter = painterResource(resource = Res.drawable.ic_phone),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp),
            )
        }

    }
}