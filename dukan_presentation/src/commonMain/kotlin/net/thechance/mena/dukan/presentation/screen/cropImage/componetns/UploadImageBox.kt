package net.thechance.mena.dukan.presentation.screen.cropImage.componetns

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.images.ImageSrc
import com.attafitamim.krop.filekit.toImageSrc
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.click_to_upload
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.ic_add_image
import mena.dukan_presentation.generated.resources.ic_edit
import mena.dukan_presentation.generated.resources.upload_dukan_image
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.image.MenaImage
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun UploadImageContainer(
    onClick: (image: ImageSrc) -> Unit,
    image: ImageBitmap?,
    onBottomIconClick: (image: ImageSrc) -> Unit,
    showBottomIcon: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
    val borderColor = Theme.colorScheme.brand.brand
    val radius = Theme.radius.xl
    val scope = rememberCoroutineScope()

    val filePicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        file?.let { image ->
            scope.launch {
                image.toImageSrc()?.let { src ->
                    onClick(src)
                }
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(radius))
                .drawWithContent {
                    drawContent()
                    drawRoundRect(
                        color = borderColor,
                        style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                        cornerRadius = CornerRadius(radius.toPx())
                    )
                }
                .clickable { filePicker.launch() },
            contentAlignment = Alignment.Center
        ) {

            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = stringResource(Res.string.dukan_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MenaImage(
                        painter = painterResource(Res.drawable.ic_add_image),
                        contentDescription = stringResource(Res.string.upload_dukan_image)
                    )
                    MenaText(
                        text = stringResource(Res.string.click_to_upload),
                        color = Theme.colorScheme.primary.primary,
                        style = Theme.typography.label.medium
                    )
                }
            }
        }
        if (showBottomIcon) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
                    .clip(shape = RoundedCornerShape(Theme.radius.full))
                    .background(Theme.colorScheme.primary.primary)
                    .border(
                        width = 1.dp,
                        color = Theme.colorScheme.background.surface,
                        shape = RoundedCornerShape(radius)
                    )
                    .clickable { filePicker.launch() },
                contentAlignment = Alignment.Center
            ) {
                MenaIcon(
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = "Bottom Action",
                    tint = Theme.colorScheme.primary.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun UploadImageContainerPreview() {
    MenaTheme {
        UploadImageContainer(onClick = {}, null, onBottomIconClick = {})
    }
}