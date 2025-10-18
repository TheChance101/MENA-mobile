package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.back
import mena.identity_presentation.generated.resources.ic_arrow_left
import mena.identity_presentation.generated.resources.image_preview
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.screen.imageCropper.components.ImageCropperComponent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import sv.lib.squircleshape.SquircleShape

class ImageCropperScreen(
    private val image: ImageBitmap,
    private val onResult: (ImageBitmap) -> Unit
) : BaseScreen<
        ImageCropperViewModel,
        ImageCropperScreenState,
        ImageCropperScreenEffect,
        ImageCropperInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(
            viewModel = getScreenModel(parameters = { parametersOf(image) })
        )
    }

    @Composable
    override fun OnRender(
        state: ImageCropperScreenState,
        listener: ImageCropperInteractionListener
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Theme.colorScheme.background.surface)
                .verticalScroll(rememberScrollState())
        ) {
            AppBar(
                title = stringResource(Res.string.image_preview),
                leadingContent = {
                    Image(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back),
                        modifier = Modifier
                            .clip(SquircleShape(Theme.radius.md))
                            .size(40.dp)
                            .clickable(onClick = listener::onNavigateBack)
                    )
                }
            )

            Spacer(Modifier.weight(0.2f))

            ImageCropperComponent(
                image = BitmapPainter(state.imageBitmap),
                onSaveButtonClicked = listener::onCropImage,
                onUploadAnotherImageClicked = listener::onChangeImage,
            )
        }
    }

    override fun onEffect(effect: ImageCropperScreenEffect, navigator: Navigator) {
        when (effect) {
            is ImageCropperScreenEffect.NavigateBackToEditProfile -> navigator.pop()
            is ImageCropperScreenEffect.NavigateBackToEditProfileWithImage -> {
                onResult(effect.imageBytes)
                navigator.pop()
            }
        }
    }
}