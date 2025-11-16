package net.thechance.mena.identity.presentation.screen.editProfile.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.camera
import mena.identity_presentation.generated.resources.image
import mena.identity_presentation.generated.resources.image_remove
import mena.identity_presentation.generated.resources.profile_image
import mena.identity_presentation.generated.resources.remove_profile_image
import mena.identity_presentation.generated.resources.take_new_image
import mena.identity_presentation.generated.resources.upload_image
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun ScaffoldScope.GetImageDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUploadImage: () -> Unit,
    onTakeImageFromCamera: () -> Unit,
    onRemoveImage: () -> Unit,
) {
    BasicDialog(
        isVisible = isVisible,
        onDismiss = onDismiss,
        onCancelClick = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = Theme.spacing._12),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.profile_image),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center,
            )

            Option(
                modifier = Modifier.padding(top = Theme.spacing._24),
                painter = painterResource(Res.drawable.image),
                title = stringResource(Res.string.upload_image),
                onClick = {
                    onUploadImage()
                    onDismiss()
                }
            )
            Option(
                painter = painterResource(Res.drawable.camera),
                title = stringResource(Res.string.take_new_image),
                onClick = {
                    onTakeImageFromCamera()
                    onDismiss()
                }
            )
            Option(
                painter = painterResource(Res.drawable.image_remove),
                title = stringResource(Res.string.remove_profile_image),
                onClick = {
                    onRemoveImage()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun Option(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication(),
                onClick = onClick
            )
            .padding(Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painter,
            contentDescription = null,
            tint = Theme.colorScheme.shadePrimary
        )

        Text(
            modifier = Modifier.padding(start = Theme.spacing._8),
            text = title,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Preview
@Composable
fun GetImageDialogPreview() {
    MenaTheme {
        Scaffold(
            overlays = {
                dialog(true) {
                    GetImageDialog(
                        isVisible = it,
                        onDismiss = {},
                        onUploadImage = {},
                        onTakeImageFromCamera = {},
                        onRemoveImage = {}
                    )
                }
            }
        ) {

        }
    }
}