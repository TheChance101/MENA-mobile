package net.thechance.mena.faith.presentation.feature.mosque.component

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.click_to_upload
import mena.faith_presentation.generated.resources.empty_state_bookmark_image
import mena.faith_presentation.generated.resources.ic_add_image
import mena.faith_presentation.generated.resources.ic_edit
import mena.faith_presentation.generated.resources.upload_mosque_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadImageContainer(
    onClick: (ImageSrc) -> Unit,
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var isPickerOpen by remember { mutableStateOf(false) }

    val filePicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        file?.let { selectedFile ->
            scope.launch {
                selectedFile.toImageSrc()?.let(onClick)
            }
        }
        isPickerOpen = false
    }

    fun handleImagePick() {
        if (!isPickerOpen) {
            isPickerOpen = true
            filePicker.launch()
        }
    }

    Box(modifier = modifier) {
        ImageUploadArea(
            image = image,
            isPickerOpen = isPickerOpen,
            onImagePick = ::handleImagePick
        )

        if (image != null) {
            EditButton(
                isPickerOpen = isPickerOpen,
                onImagePick = ::handleImagePick
            )
        }
    }
}

@Composable
private fun ImageUploadArea(
    image: ImageBitmap?,
    isPickerOpen: Boolean,
    onImagePick: () -> Unit,
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
    val borderColor = Theme.colorScheme.brand.brand
    val radius = Theme.radius.xl

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(radius))
            .drawWithContent {
                drawContent()
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                    cornerRadius = CornerRadius(radius.toPx())
                )
            }
            .clickable(enabled = !isPickerOpen, onClick = onImagePick),
        contentAlignment = Alignment.Center
    ) {
        if (image != null) {
            UploadedImage(image = image)
        } else {
            EmptyImagePlaceholder()
        }
    }
}

@Composable
private fun UploadedImage(image: ImageBitmap) {
    Image(
        bitmap = image,
        contentDescription = stringResource(Res.string.empty_state_bookmark_image),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun EmptyImagePlaceholder() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(Res.drawable.ic_add_image),
            contentDescription = stringResource(Res.string.upload_mosque_image)
        )
        Text(
            text = stringResource(Res.string.click_to_upload),
            color = Theme.colorScheme.primary.primary,
            style = Theme.typography.label.medium
        )
    }
}

@Composable
private fun EditButton(
    isPickerOpen: Boolean,
    onImagePick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .offset(y = (-20).dp)
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.primary.primary)
            .border(
                width = 1.dp,
                color = Theme.colorScheme.background.surface,
                shape = RoundedCornerShape(Theme.radius.full)
            )
            .clickable(enabled = !isPickerOpen, onClick = onImagePick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_edit),
            contentDescription = "Bottom Action",
            tint = Theme.colorScheme.primary.onPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun UploadImageContainerPreview() {
    MenaTheme {
        QuranTheme {
            UploadImageContainer(
                onClick = {},
                image = null
            )
        }
    }
}