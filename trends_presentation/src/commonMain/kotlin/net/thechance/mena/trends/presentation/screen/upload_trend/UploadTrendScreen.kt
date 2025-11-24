package net.thechance.mena.trends.presentation.screen.upload_trend

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
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.new_trend
import mena.trends_presentation.generated.resources.upload_video
import mena.trends_presentation.generated.resources.upload_video_description
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.toStringResource
import net.thechance.mena.trends.presentation.shared.component.BackIcon
import net.thechance.mena.trends.presentation.shared.component.NextButton
import net.thechance.mena.trends.presentation.shared.component.TrendsAnimatedVisibility
import net.thechance.mena.trends.presentation.shared.component.UploadPageNumber
import net.thechance.mena.trends.presentation.shared.component.UploadVideoCard
import net.thechance.mena.trends.presentation.shared.component.VideoLoadingCardItem
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.model.SnackBarStatus
import net.thechance.mena.trends.presentation.shared.model.VideoAction
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import net.thechance.mena.trends.presentation.shared.util.isIdle
import net.thechance.mena.trends.presentation.snackbar.LocalSnackbarController
import net.thechance.mena.trends.presentation.snackbar.SnackBarData
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UploadTrendScreen(
    viewModel: UploadTrendViewModel = koinViewModel()
) {

    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    val snackBarController = LocalSnackbarController.current

    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Video,
        onResult = { file ->
            launchFilePicker(
                file = file,
                coroutineScope = coroutineScope,
                onRetrieveVideo = viewModel::onRetrieveVideo
            )
        }
    )

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is UploadTrendScreenEffect.NavigateToAddDescription -> {
                navController.navigate(Route.VideoDescription(effect.trendId))
            }

            is UploadTrendScreenEffect.ShowErrorSnackbar -> {
                snackBarController.showSnackBar(
                    SnackBarData(
                        message = getString(effect.errorState.toStringResource()),
                        snackBarType = SnackBarStatus.Error,
                    )
                )
            }

            UploadTrendScreenEffect.NavigateBack -> navController.popBackStack()
            UploadTrendScreenEffect.LaunchFilePicker -> launcher.launch()
        }
    }

    UploadTrendScreenContent(
        state = screenState,
        listener = viewModel
    )
}

@Composable
private fun UploadTrendScreenContent(
    state: UploadTrendScreenState,
    listener: UploadTrendInteractionListener
) {
    Scaffold(
        topBar = { UploadTrendScreenTopBar(onBackClick = listener::onClickBack) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing._16)
        ) {
            UploadTrendScreenHeader(Modifier.padding(top = Theme.spacing._16))

            UploadVideoCard(
                modifier = Modifier.padding(top = Theme.spacing._24),
                thumbnail = state.thumbnail,
                isEnabled = state.isUploadVideoCardEnabled,
                isLoading = state.isThumbnailLoading,
                onCardClick = listener::onClickUploadCard,
                onEditClick = listener::onClickUploadCard
            )
            TrendsAnimatedVisibility(visible = !state.uploadingState.isIdle) {
                VideoLoadingCardItem(
                    modifier = Modifier.padding(
                        top = state.thumbnail?.let { Theme.spacing._24 } ?: Theme.spacing._8
                    ),
                    title = state.selectedFile.name,
                    sizeUploaded = state.sizeUploaded,
                    videoSize = state.selectedFile.sizeText,
                    uploadingState = state.uploadingState,
                    progress = state.uploadingProgress,
                ) { action ->
                    when (action) {
                        VideoAction.Cancel -> listener.onClickCancelUpload()
                        VideoAction.Retry -> listener.onClickRetryUpload()
                        VideoAction.Delete -> listener.onClickDeleteVideo()
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                modifier = Modifier.padding(vertical = Theme.spacing._24),
                onNextClick = listener::onClickNext,
                isButtonEnabled = state.isNextButtonEnabled,
                isButtonLoading = state.isNextButtonLoading
            )
        }
    }
}

private fun launchFilePicker(
    file: PlatformFile?,
    coroutineScope: CoroutineScope,
    onRetrieveVideo: (file: FileUiState) -> Unit
) {
    file?.let {
        coroutineScope.launch {
            val fileState = FileUiState(
                filePath = file.path,
                name = file.name,
                size = file.size(),
            )
            onRetrieveVideo(fileState)
        }
    }
}

@Composable
private fun UploadTrendScreenTopBar(
    onBackClick: () -> Unit
) {
    AppBar(
        onLeadingClick = onBackClick,
        leadingContent = {
            BackIcon()
        },
        title = stringResource(Res.string.new_trend),
        trailingContent = { UploadPageNumber(page = 1) }
    )
}

@Composable
private fun UploadTrendScreenHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.upload_video),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
        )

        Text(
            modifier = Modifier.padding(top = Theme.spacing._4),
            text = stringResource(Res.string.upload_video_description),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}