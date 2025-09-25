package net.thechance.mena.trends.presentation.screen.upload_trend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.launch
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_eye
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.base.toStringResource
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


// TODO: this screen is just for testing
@Composable
internal fun UploadTrendScreen(viewModel: UploadTrendViewModel = koinViewModel()) {

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {

            is UploadTrendsScreenEffect.ShowSnackBar -> {}
            is UploadTrendsScreenEffect.NavigateToDescription -> {
                // TODO
            }

            UploadTrendsScreenEffect.OpenFilePicker -> {

            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Video,
        onResult = { file ->
            file?.let {
                coroutineScope.launch {
                    val fileMeta = UploadTrendsScreenState.SelectedFileMeta(
                        name = file.name,
                        extension = file.extension,
                        sizeInBytes = file.size()
                    )
                    viewModel.onSelectFile(fileMeta) { file.readBytes() }
                }
            }
        }
    )

    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().safeContentPadding().padding(16.dp),
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.errorState?.let {
                SnackBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Failed",
                    message = stringResource(it.toStringResource()),
                    leadingIcon = painterResource(Res.drawable.ic_eye)
                )
            }

            Button(
                onClick = { launcher.launch() }
            ) {
                Text(text = "Upload Trend", style = Theme.typography.title.medium)
            }

            when (state.uploadingState) {
                UploadTrendsScreenState.UploadingState.IDLE -> {
                    Text(
                        "Status: Idle", style = Theme.typography.body.medium,
                        color = Color.Gray
                    )
                }

                UploadTrendsScreenState.UploadingState.UPLOADING -> {
                    Text("Status: Uploading...", style = Theme.typography.body.medium)
                }

                UploadTrendsScreenState.UploadingState.SUCCESS -> {
                    Text(
                        "Status: Upload Successful",
                        style = Theme.typography.body.medium,
                        color = Color.Green
                    )
                }

                UploadTrendsScreenState.UploadingState.FAILED -> {
                    Text(
                        "Status: Upload Failed",
                        style = Theme.typography.body.medium,
                        color = Color.Red
                    )
                }
            }

            if (state.uploadingState == UploadTrendsScreenState.UploadingState.UPLOADING) {
                Text("${state.uploadingProgress}%", style = Theme.typography.body.small)
                Text("${state.uploadedMegaBytes} MB", style = Theme.typography.body.small)
            }

            if (state.selectedFileMeta.name.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "File Details",
                        style = Theme.typography.title.small
                    )

                    Text(
                        "Name: ${state.selectedFileMeta.name}",
                        style = Theme.typography.body.small
                    )
                    Text(
                        "Size: ${state.selectedFileMeta.size}",
                        style = Theme.typography.body.small
                    )
                    Text(
                        "MIME Type: ${state.selectedFileMeta.extension}",
                        style = Theme.typography.body.small
                    )
                    Text(
                        "Duration: ${state.selectedFileMeta.duration} ms",
                        style = Theme.typography.body.small
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.onCancelUploadClick() },
                ) {
                    Text(text = "Cancel", style = Theme.typography.title.medium)
                }

                Button(
                    onClick = { viewModel.onNextClick() },
                ) {
                    Text(text = "Next", style = Theme.typography.title.medium)
                }

                Button(
                    onClick = { viewModel.onRetryClick() },
                ) {
                    Text(text = "Retry", style = Theme.typography.title.medium)
                }
            }
        }
    }
}