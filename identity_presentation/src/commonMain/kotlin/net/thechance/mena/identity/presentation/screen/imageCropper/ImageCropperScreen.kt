package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.decodeToImageBitmap
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
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.imageCropper.components.ImageCropperComponent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import sv.lib.squircleshape.SquircleShape

class ImageCropperScreen(
    private val imageKey: String,
    private val onResult: (String) -> Unit,
) : BaseScreen<
    ImageCropperViewModel,
    ImageCropperScreenState,
    ImageCropperScreenEffect,
    ImageCropperInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(
            viewModel = getScreenModel(parameters = { parametersOf(imageKey) })
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
            ScreenAppBar(onNavigateBack = listener::onNavigateBack)

            Spacer(Modifier.weight(0.2f))

            state.imageByteArray?.let {
                ImageCropperComponent(
                    image = BitmapPainter(it.decodeToImageBitmap()),
                    onSaveButtonClicked = listener::onCropImage,
                    onUploadAnotherImageClicked = listener::onChangeImage,
                )
            }
        }
    }

    override fun onEffect(
        effect: ImageCropperScreenEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            is ImageCropperScreenEffect.NavigateBackToEditProfile -> navigator.pop()

            is ImageCropperScreenEffect.NavigateBackToEditProfileWithImage -> {
                onResult(effect.imageKey)
                navigator.pop()
            }

            is ImageCropperScreenEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }
}

@Composable
private fun ScreenAppBar(onNavigateBack: () -> Unit) {
    AppBar(
        title = stringResource(Res.string.image_preview),
        modifier = Modifier.statusBarsPadding(),
        leadingContent = {
            Image(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back),
                modifier = Modifier
                    .clip(SquircleShape(Theme.radius.md))
                    .size(40.dp)
                    .clickable(onClick = onNavigateBack)
            )
        }
    )
}