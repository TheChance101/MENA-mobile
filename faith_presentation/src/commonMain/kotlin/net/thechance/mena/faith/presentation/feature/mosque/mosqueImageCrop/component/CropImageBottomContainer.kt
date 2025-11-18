package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.images.ImageSrc
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CropImageBottomContainer(
    onUploadAnotherImageClicked: (imageSrc: ImageSrc?) -> Unit,
    onSaveClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        PrimaryButton(
            text = stringResource(Res.string.save),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = onSaveClicked,
        )
        UploadAnotherImageButton(
            onClick = onUploadAnotherImageClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            CropImageBottomContainer(
                onUploadAnotherImageClicked = {},
                onSaveClicked = {}
            )
        }
    }
}
