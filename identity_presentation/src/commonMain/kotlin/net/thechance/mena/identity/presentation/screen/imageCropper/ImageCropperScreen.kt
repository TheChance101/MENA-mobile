package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.back
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ic_arrow_left
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.image_preview
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.screen.imageCropper.components.ImageCropperComponent
import org.jetbrains.compose.resources.StringResource
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
        ScreenLayout(errorMessage = state.errorMessage) {
            Screen(state = state, listener = listener)
        }
    }

    override fun onEffect(effect: ImageCropperScreenEffect, navigator: Navigator) {
        when (effect) {
            is ImageCropperScreenEffect.NavigateBackToEditProfile -> navigator.pop()
            is ImageCropperScreenEffect.NavigateBackToEditProfileWithImage -> {
                onResult(effect.imageKey)
                navigator.pop()
            }
        }
    }

    @Composable
    private fun ScreenLayout(
        errorMessage: StringResource?,
        content: @Composable () -> Unit
    ) {
        Scaffold(
            snakeBar = {
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SnackBar(
                        title = stringResource(Res.string.error),
                        message = errorMessage?.let { stringResource(it) } ?: "",
                        leadingIcon = painterResource(Res.drawable.ic_close_circle),
                        modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._16)
                            .padding(horizontal = Theme.spacing._16)
                    )
                }
            },
            content = content
        )
    }

    @Composable
    private fun Screen(
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

            state.imageBitmap?.let {
                ImageCropperComponent(
                    image = BitmapPainter(it),
                    onSaveButtonClicked = listener::onCropImage,
                    onUploadAnotherImageClicked = listener::onChangeImage,
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