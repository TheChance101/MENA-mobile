package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.createDukan.components.ImageCroppingBox
import net.thechance.mena.dukan.presentation.screen.createDukan.components.SaveButton
import net.thechance.mena.dukan.presentation.screen.createDukan.components.UploadAnotherImageButton
import net.thechance.mena.dukan.presentation.screen.createDukan.components.ZoomControls
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanImageCropContent(
    state: CreateDukanUiState,
    interactionListener: CreateDukanInteractionListener
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceHigh)
            .statusBarsPadding()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ImageCroppingBox(
                content = {},
                modifier = Modifier.padding(top = Theme.spacing._24)
            )
        }

        item {
            ZoomControls(
                onZoomInClicked = interactionListener::onZoomInClicked,
                onZoomOutClicked = interactionListener::onZoomOutClicked,
                onResetClicked = interactionListener::onResetClicked,
                modifier = Modifier.padding(top = Theme.spacing._12),
                isZoomOutEnabled = state.isZoomOutEnabled
            )
        }

        item {
            SaveButton(
                onClick = interactionListener::onSaveClicked,
                modifier = Modifier.padding(top = Theme.spacing._12)
            )
        }

        item {
            UploadAnotherImageButton(
                onClick = interactionListener::onUploadAnotherImageClicked,
                modifier = Modifier.padding(top = Theme.spacing._12)
            )
        }
    }
}

@Preview
@Composable
private fun DukanImageCropContentPreview() {
    MenaTheme {
        DukanImageCropContent(
            state = CreateDukanUiState(),
            interactionListener = PreviewCreateDukanInteractionListener
        )
    }
}