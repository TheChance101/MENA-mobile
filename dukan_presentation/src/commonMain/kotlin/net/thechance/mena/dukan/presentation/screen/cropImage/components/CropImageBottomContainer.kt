package net.thechance.mena.dukan.presentation.screen.cropImage.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.images.ImageSrc
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun CropImageBottomContainer(
    onUploadAnotherImageClicked: (imageSrc: ImageSrc?) -> Unit,
    onSaveClicked: () -> Unit,
) {
    PrimaryButton(
        text = stringResource(Res.string.save),
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = Theme.spacing._16),
        onClick = onSaveClicked,
    )
    UploadAnotherImageButton(
        onClick = onUploadAnotherImageClicked,
        modifier = Modifier.padding(top = Theme.spacing._8, bottom = 18.dp)
            .padding(horizontal = Theme.spacing._16)
    )
}