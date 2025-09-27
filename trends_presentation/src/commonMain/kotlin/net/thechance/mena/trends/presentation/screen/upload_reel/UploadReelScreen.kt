package net.thechance.mena.trends.presentation.screen.upload_reel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.launch
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.new_trend
import mena.trends_presentation.generated.resources.upload_video
import mena.trends_presentation.generated.resources.upload_video_description
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.component.UploadVideoCard
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UploadReelScreen(
    viewModel: UploadReelViewModel = koinViewModel()
) {

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is UploadReelScreenEffect.NavigateToAddDescription -> {} // TODO
            UploadReelScreenEffect.NavigateBack -> {} // TODO()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // TODO/ launcher usage: onClick = { launcher.launch() }
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Video,
        onResult = { file ->
            file?.let {
                coroutineScope.launch {
                    val fileState = FileUiState(
                        name = file.name,
                        extension = file.extension,
                        sizeInBytes = file.size()
                    )
                    viewModel.onRetrieveVideo(fileState) { file.readBytes() }
                }
            }
        }
    )
}

@Composable
private fun UploadReelScreenContent(
    state: UploadReelScreenState,
    listener: UploadReelInteractionListener
) {
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

            UploadVideoCard() // TODO::::
        }
    }
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
    )
}