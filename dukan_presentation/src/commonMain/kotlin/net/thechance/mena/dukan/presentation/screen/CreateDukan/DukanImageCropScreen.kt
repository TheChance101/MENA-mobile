package net.thechance.mena.dukan.presentation.screen.CreateDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.CreateDukan.components.ImageCroppingBox
import net.thechance.mena.dukan.presentation.screen.CreateDukan.components.SaveButton
import net.thechance.mena.dukan.presentation.screen.CreateDukan.components.UploadAnotherImageButton
import net.thechance.mena.dukan.presentation.screen.CreateDukan.components.ZoomControls
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanImageCropScreen() {
}

@Composable
fun DukanImageCropContent(
    state: CreateDukanUiState,
    interactionListener: CreateDukanInteractionListener
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceHigh)
            .statusBarsPadding()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(title = "Dukan image")
        ImageCroppingBox(
            content = {},
            modifier = Modifier.padding(top = Theme.spacing._24)
        )
        ZoomControls(
            onZoomInClicked = interactionListener::onZoomInClicked,
            onZoomOutClicked = interactionListener::onZoomOutClicked,
            onResetClicked = interactionListener::onResetClicked,
            modifier = Modifier.padding(top = Theme.spacing._12)
        )
        SaveButton(
            onClick = interactionListener::onSaveClicked,
            modifier = Modifier.padding(top = Theme.spacing._12)
        )
        UploadAnotherImageButton(
            onClick = interactionListener::onUploadAnotherImageClicked,
            modifier = Modifier.padding(top = Theme.spacing._12)
        )
    }
}

@Preview
@Composable
fun DukanImageCropContentPreview() {
    MenaTheme {
        DukanImageCropContent(
            state = CreateDukanUiState(),
            interactionListener = object : CreateDukanInteractionListener {
                override fun onButtonClicked() {}

                override fun onBackClicked() {}

                override fun onClickUploadImage() {}

                override fun onClickEditImage() {}

                override fun onCLickNext() {}

                override fun onSaveClicked() {}

                override fun onZoomInClicked() {}

                override fun onZoomOutClicked() {}

                override fun onResetClicked() {}

                override fun onUploadAnotherImageClicked() {}
            }
        )
    }
}