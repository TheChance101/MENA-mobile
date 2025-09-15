package net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_image
import mena.dukan_presentation.generated.resources.`style has image`
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanItemPlaceholder(
    modifier: Modifier = Modifier,
    contentPadding: Dp
){

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.xxs))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        MenaIcon(
            painter = painterResource(Res.drawable.ic_image),
            tint = Theme.colorScheme.stroke,
            contentDescription = stringResource(Res.string.`style has image`),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview
@Composable
private fun DukanItemImagePreview(){
    DukanItemPlaceholder(
        contentPadding = 9.dp
    )
}