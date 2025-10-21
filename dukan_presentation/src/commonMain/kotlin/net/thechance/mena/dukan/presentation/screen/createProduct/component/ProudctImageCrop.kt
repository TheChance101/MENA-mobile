package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.images.ImageSrc
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.cropImage.ImageCropScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProductImageCropScreen(
    isVisible: Boolean,
    selectedImage: ImageSrc?,
    aspectRatio: Float,
    onCropImageBack: (croppedImage: ImageBitmap) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        label = "Crop Product Image"
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = Theme.spacing._16)
                .fillMaxSize()
        ) {
            AppBar(
                title = "Product Image", // Todo ( until the design of cropping product image finish )
                titleColor = Theme.colorScheme.shadePrimary,
                modifier = Modifier
                    .background(color = Theme.colorScheme.background.surface)
                    .padding(bottom = Theme.spacing._16)
                    .padding(horizontal = Theme.spacing._16),
                contentPadding = PaddingValues(0.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(resource = Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow)
                    )
                },
                onLeadingClick = onBack,
            )
            ImageCropScreen(
                selectedImage = selectedImage,
                onImageCrop = onCropImageBack,
                aspectRatio = aspectRatio
            )
        }
    }
}
