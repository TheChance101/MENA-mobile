@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.dukan.presentation.component.product.productImage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add_image
import mena.dukan_presentation.generated.resources.upload_dukan_image
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.drawBorder
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.util.file.PlatformImageFile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun UploadProductImage(
    modifier: Modifier = Modifier,
    onUploadImageClick: (imageFile: ImageFile) -> Unit = {},
    isUploadingImageEnabled: Boolean = false
) {

    val borderColor = Theme.colorScheme.primary.primary
    val cornerRadiusValue = Theme.radius.md
    val scope = rememberCoroutineScope()

    val debounceTimeMillis = 1000L
    val lastLaunchTimeMillis = remember { mutableStateOf(0L) }
    val isFilePickerLaunching = remember { mutableStateOf(false) }
    val filePickerLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        isFilePickerLaunching.value = false
        file?.let { imageFile ->
            scope.launch {
                onUploadImageClick(PlatformImageFile(imageFile))
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

    Box(
        modifier = modifier
            .size(size = 88.dp)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = SquircleShape(cornerRadiusValue)
            )
            .clip(SquircleShape(cornerRadiusValue))
            .clickable(onClick = { safeLaunch() }, enabled = isUploadingImageEnabled)
            .drawWithContent {
                drawContent()
                drawBorder(
                    borderColor = borderColor,
                    cornerRadiusValue = cornerRadiusValue.toPx()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(size = 24.dp),
            painter = painterResource(resource = Res.drawable.ic_add_image),
            contentDescription = stringResource(resource = Res.string.upload_dukan_image),
        )
    }
}

@Preview
@Composable
private fun DefaultContainerImagePreview() {
    MenaTheme {
        Box(
            modifier = Modifier.size(150.dp)
                .background(color = Theme.colorScheme.background.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            UploadProductImage()
        }
    }
}