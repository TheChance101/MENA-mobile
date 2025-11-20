@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.dukan.presentation.screen.cropImage.components

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
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
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.util.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape
import sv.lib.squircleshape.drawSquircle
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@Composable
fun UploadImageContainer(
    onClick: (image: ImageSrc) -> Unit,
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
    val borderColor = Theme.colorScheme.brand.brand
    val radius = Theme.radius.xl
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
                if (now - lastLaunchTimeMillis.value > debounceTimeMillis) {
                    lastLaunchTimeMillis.value = now
                    isFilePickerLaunching.value = true
                    filePickerLauncher.launch()
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
                .clip(SquircleShape(radius))
                .background(Theme.colorScheme.primary.onPrimary)
                .drawWithContent {
                    drawContent()
                    drawSquircle(
                        color = borderColor,
                        size = size,
                        topLeftCorner = radius.toPx(),
                        topRightCorner = radius.toPx(),
                        bottomLeftCorner = radius.toPx(),
                        bottomRightCorner = radius.toPx(),
                        smoothing = CornerSmoothing.Medium,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = dashEffect
                        )
                    )
                }
                .clickable(
                    onClick = { safeLaunch() },
                    indication = null,
                    interactionSource = null
                ),
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
                    Image(
                        painter = painterResource(Res.drawable.ic_add_image),
                        contentDescription = stringResource(Res.string.upload_dukan_image)
                    )
                    Text(
                        text = stringResource(Res.string.click_to_upload),
                        color = Theme.colorScheme.primary.primary,
                        style = Theme.typography.label.medium,
                        modifier = Modifier.padding(start = 18.dp)
                    )
                }
            }
        }
        if (image != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
                    .clip(RoundedCornerShape(radius))
                    .background(Theme.colorScheme.primary.primary)
                    .border(
                        width = 1.dp,
                        color = Theme.colorScheme.background.surface,
                        shape = RoundedCornerShape(radius)
                    )
                    .clickable { safeLaunch() },
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
    }
}

@Preview
@Composable
private fun UploadImageContainerPreview() {
    MenaTheme(appTheme = AppTheme.DARK.name) {
        UploadImageContainer(onClick = {}, null)
    }
}