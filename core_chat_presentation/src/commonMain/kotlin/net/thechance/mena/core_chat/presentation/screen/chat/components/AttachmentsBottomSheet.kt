package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.camera
import mena.core_chat_presentation.generated.resources.ic_camera
import mena.core_chat_presentation.generated.resources.ic_cancel
import mena.core_chat_presentation.generated.resources.ic_gallery
import mena.core_chat_presentation.generated.resources.photo
import net.thechance.mena.core_chat.presentation.screen.chat.AttachmentsInteractionListener
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AttachmentsBottomSheet(
    modifier: Modifier = Modifier,
    attachmentsInteractionListener: AttachmentsInteractionListener,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape, shadow = Shadow(
                    radius = Theme.spacing._12,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = .06f),
                    offset = DpOffset(0.dp, (-2).dp)
                )
            )
            .background(
                color = Theme.colorScheme.background.surface, shape = RectangleShape
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        AttachmentBottomSheetContent(
            modifier = Modifier.fillMaxWidth(),
            attachmentsInteractionListener = attachmentsInteractionListener
        )

        FabButton(
            painter = painterResource(Res.drawable.ic_cancel),
            shape = RoundedCornerShape(Theme.spacing._12),
            containerColor = Theme.colorScheme.background.surface,
            contentColor = Theme.colorScheme.primary.primary,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Theme.colorScheme.stroke,
                    shape = RoundedCornerShape(Theme.spacing._12)
                )
                .align(Alignment.End),
            onClick = attachmentsInteractionListener::onCancelClicked
        )
    }
}

@Composable
private fun AttachmentBottomSheetContent(
    attachmentsInteractionListener: AttachmentsInteractionListener,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AttachmentsBottomSheetItem(
            iconRes = Res.drawable.ic_gallery,
            titleRes = Res.string.photo,
            onClick = attachmentsInteractionListener::onPhotoClicked
        )

        AttachmentsBottomSheetItem(
            iconRes = Res.drawable.ic_camera,
            titleRes = Res.string.camera,
            onClick = attachmentsInteractionListener::onCameraClicked
        )
    }
}

@Composable
@Preview()
private fun PreviewAddPhotoBottomSheet() {

    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {
            AttachmentsBottomSheet(
                modifier = Modifier.align(Alignment.BottomCenter),
                attachmentsInteractionListener = object : AttachmentsInteractionListener {
                    override fun onPhotoClicked() {}
                    override fun onCameraClicked() {}
                    override fun onCancelClicked() {}
                }
            )

        }
    }
}
