@file:OptIn(ExperimentalTime::class)
package net.thechance.mena.dukan.presentation.screen.cropImage.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.images.ImageSrc
import com.attafitamim.krop.filekit.toImageSrc
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.upload_another_image
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun UploadAnotherImageButton(
    onClick: (imageSrc: ImageSrc?) -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    val debounceTimeMillis = 1000L
    val lastLaunchTimeMillis = remember { mutableStateOf(0L) }
    val isFilePickerLaunching = remember { mutableStateOf(false) }
    val filePickerLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        isFilePickerLaunching.value = false
        file?.let { image ->
            scope.launch {
                image.toImageSrc()?.let { src ->
                    onClick(src)
                }
            }
        }
    }
    val safeLaunch: () -> Unit = remember {
        {
            if (isFilePickerLaunching.value.not()) {
                val now = Clock.System.now().toEpochMilliseconds()
                if (now - lastLaunchTimeMillis.value > debounceTimeMillis ) {
                    lastLaunchTimeMillis.value = now
                    isFilePickerLaunching.value = true
                    filePickerLauncher.launch()
                }
            }
        }
    }

    OutlinedButton(
        text = stringResource(Res.string.upload_another_image),
        trailingIcon = ColorPainter(Color.Transparent),
        onClick = { safeLaunch() },
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