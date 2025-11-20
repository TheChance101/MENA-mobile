package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.attafitamim.krop.core.images.ImageSrc
import com.attafitamim.krop.filekit.toImageSrc
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.upload_another_image
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun UploadAnotherImageButton(
    onClick: (imageSrc: ImageSrc?) -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
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
    OutlinedButton(
        text = stringResource(Res.string.upload_another_image),
        onClick = { filePicker.launch() },
        modifier = modifier,
        shape = RoundedCornerShape(Theme.radius.md),
        isEnabled = isEnabled,
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Box(
                contentAlignment = Alignment.Center
            ) {
                UploadAnotherImageButton(onClick = {})
            }
        }
    }
}
