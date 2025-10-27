package net.thechance.mena.identity.presentation.screen.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.coroutines.launch
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.button_skip_for_now
import mena.identity_presentation.generated.resources.button_upload
import mena.identity_presentation.generated.resources.desc_upload_profile_image
import mena.identity_presentation.generated.resources.title_complete_your_profile
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.PageDescription
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PickProfileImgView(
    imageBitmap: ImageBitmap?,
    onUploadClick: () -> Unit = {},
    onSkipClick: () -> Unit = {},
    onEditClick: (imageBitmap: ImageBitmap) -> Unit = {}
) {
    val isImageLoaded = imageBitmap != null
    val scope = rememberCoroutineScope()
    val galleryPicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        file?.let { image ->
            scope.launch { onEditClick(image.toImageBitmap()) }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
    ) {
        AuthScreenContainer(
            modifier = Modifier
                .fillMaxSize()
        ) {

            PageDescription(
                title = stringResource(Res.string.title_complete_your_profile),
                subtitle = stringResource(Res.string.desc_upload_profile_image),
            )

            UploadImageContainer(
                onClick = { galleryPicker.launch() },
                image = imageBitmap,
                modifier = Modifier
                    .width(328.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(Res.string.button_upload),
                onClick = { onUploadClick() },
                isEnabled = isImageLoaded,
                contentPadding = PaddingValues(vertical = 13.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Theme.spacing._12)
            )

            TextButton(
                text = stringResource(Res.string.button_skip_for_now),
                onClick = { onSkipClick() },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun PickProfileImgViewPreview() {
    val imageBitmap = mutableStateOf<ImageBitmap?>(null)
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {

            PickProfileImgView(
                imageBitmap = imageBitmap.value,
                onEditClick = { imageBitmap.value = it },
            )
        }
    }
}

@Preview
@Composable
private fun PickProfileImgViewPreview2() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {

            PickProfileImgView(
                imageBitmap = ImageBitmap(1, 1),
            )
        }
    }
}