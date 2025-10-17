package net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.heart_icon
import mena.dukan_presentation.generated.resources.ic_favorite
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditorPickDukanItem(
    dukanImage: Any,
    dukanName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(RoundedCornerShape(Theme.radius.lg))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onClick() }
            )
    ) {
        AsyncImage(
        model = dukanImage,
        contentDescription = stringResource(Res.string.dukan_image),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
        Box(
            Modifier.fillMaxSize().padding( Theme.spacing._8)
        ){
            Text(
                text = dukanName,
                style = Theme.typography.title.small,
                color = Theme.colorScheme.primary.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
            )
            Icon(
                painter = painterResource(Res.drawable.ic_favorite),
                contentDescription = stringResource(Res.string.heart_icon),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = Theme.colorScheme.primary.primary,
                        shape = RoundedCornerShape(Theme.radius.full)
                    ).padding(Theme.spacing._8)
            )
        }
    }
}

@Preview
@Composable
private fun EditorPickDukanItemPreview() {
    MenaTheme {
        EditorPickDukanItem(
            dukanName = "Calvin Klein store - Baghdad",
            dukanImage = "https://plus.unsplash.com/premium_photo-1664474619075-644dd191935f?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1469",
            onClick = {}
        )
    }
}
