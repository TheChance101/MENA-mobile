package net.thechance.mena.identity.presentation.screen.imageCropper.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.toByteArray
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.coroutines.launch
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.upload_another_image
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun UploadAnotherImageButton(
    onClick: (imageSrc: ByteArray) -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    val filePicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        file?.let { image ->
            scope.launch {
                image.toImageBitmap()
                    .toByteArray(format = ImageFormat.PNG)
                    .also(onClick)
            }
        }
    }
    OutlinedButton(
        text = stringResource(Res.string.upload_another_image),
        trailingIcon = ColorPainter(Color.Transparent),
        onClick = { filePicker.launch() },
        modifier = modifier.fillMaxWidth(),
        shape = SquircleShape(Theme.radius.md),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 13.dp),
        isEnabled = isEnabled,
    )
}

@Preview
@Composable
private fun UploadAnotherImageButtonPreview() {
    MenaTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            UploadAnotherImageButton(onClick = {})
        }
    }
}