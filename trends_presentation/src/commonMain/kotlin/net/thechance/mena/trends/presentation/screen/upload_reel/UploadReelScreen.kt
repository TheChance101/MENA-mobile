package net.thechance.mena.trends.presentation.screen.upload_reel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.new_trend
import mena.trends_presentation.generated.resources.page_number
import mena.trends_presentation.generated.resources.upload_video
import mena.trends_presentation.generated.resources.upload_video_description
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.component.NextButton
import net.thechance.mena.trends.presentation.shared.component.UploadVideoCard
import net.thechance.mena.trends.presentation.shared.component.VideoLoadingCardItem
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.model.VideoAction
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import net.thechance.mena.trends.presentation.shared.util.video_util.getMimeTypeFromExtension
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UploadReelScreen(
    viewModel: UploadReelViewModel = koinViewModel()
) {

    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is UploadReelScreenEffect.NavigateToAddDescription -> {
                navController.navigate(Route.VideoDescription(effect.id))
            }

            UploadReelScreenEffect.NavigateBack -> navController.popBackStack()
        }
    }

    UploadReelScreenContent(
        state = screenState,
        listener = viewModel
    )
}

@Composable
private fun UploadReelScreenContent(
    state: UploadReelScreenState,
    listener: UploadReelInteractionListener
) {
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Video,
        onResult = { file ->
            launchFilePicker(
                file = file,
                coroutineScope = coroutineScope,
                onRetrieveVideo = listener::onRetrieveVideo
            )
        }
    )

    Scaffold(
        topBar = { UploadReelScreenTopBar(onBackClick = listener::onBackClick) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing._16)
        ) {
            UploadReelScreenHeader(
                modifier = Modifier
                    .padding(
                        top = Theme.spacing._16,
                        bottom = Theme.spacing._24
                    )
            )

            UploadVideoCard(
                thumbnail = state.thumbnail,
                isEnabled = state.isUploadVideoCardEnabled,
                onCardClick = launcher::launch,
                onEditClick = {
                    listener.onEditVideoClick()
                    launcher.launch()
                },
                modifier = Modifier.padding(bottom = Theme.spacing._24)
            )

            VideoLoadingCardItem(
                title = "${state.selectedFile.name}.${state.selectedFile.extension}",
                videoSize = state.selectedFile.sizeInMegaBytes,
                videoState = state.uploadingTrendState,
                progress = state.uploadedBytes.toFloat() / state.selectedFile.sizeInBytes.toFloat(),
                modifier = Modifier.padding(bottom = Theme.spacing._16),
                onAction = { action ->
                    when (action) {
                        VideoAction.Cancel -> listener.onCancelUploadClick()
                        VideoAction.Retry -> listener.onRetryUploadClick()
                        VideoAction.Delete -> listener.onDeleteVideoClick()
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onNextClick = listener::onNextClick,
                isButtonEnabled = state.isNextButtonEnabled,
                isButtonLoading = state.isNextButtonLoading,
                modifier = Modifier.padding(bottom = Theme.spacing._16)
            )
        }
    }
}

private fun launchFilePicker(
    file: PlatformFile?,
    coroutineScope: CoroutineScope,
    onRetrieveVideo: (file: FileUiState, readBytes: suspend () -> ByteArray) -> Unit
) {
    file?.let {
        coroutineScope.launch {
            val fileState = FileUiState(
                name = file.nameWithoutExtension,
                extension = file.extension,
                sizeInBytes = file.size(),
                mimeType = getMimeTypeFromExtension(file.extension).orEmpty()
            )
            onRetrieveVideo(fileState) { file.readBytes() }
        }
    }
}

@Composable
private fun UploadReelScreenTopBar(
    onBackClick: () -> Unit
) {
    AppBar(
        onLeadingClick = onBackClick,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        title = stringResource(Res.string.new_trend),
        trailingContent = {
            Chip(
                text = stringResource(Res.string.page_number, 1, 3),
                isSelected = false,
                isEnabled = true,
                onClick = {},
            )
        }
    )
}

@Composable
private fun UploadReelScreenHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.upload_video),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .padding(bottom = Theme.spacing._4)
        )

        Text(
            text = stringResource(Res.string.upload_video_description),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}