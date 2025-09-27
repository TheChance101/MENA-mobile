package net.thechance.mena.trends.presentation.screen.upload_trend

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.launch
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UploadTrendScreen(viewModel: UploadTrendViewModel = koinViewModel()) {

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is UploadTrendsScreenEffect.NavigateToAddDescription -> {} // TODO
            UploadTrendsScreenEffect.NavigateBack -> {} // TODO()
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