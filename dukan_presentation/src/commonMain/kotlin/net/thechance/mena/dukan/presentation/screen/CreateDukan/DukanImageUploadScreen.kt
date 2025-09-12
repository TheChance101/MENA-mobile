package net.thechance.mena.dukan.presentation.screen.CreateDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.CreateDukan.components.NextButton
import net.thechance.mena.dukan.presentation.screen.CreateDukan.components.UploadImageContainer
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadDukanImageScreen() {
}

@Composable
fun UploadDukanImageContent(
    state: CreateDukanUiState,
    interactionListener: CreateDukanInteractionListener
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceHigh)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        AppBar(title = "Create new Dukan")
        Text(
            text = "Dukan image",
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "Upload dukan image, it should be rectangle 9:16 aspect ratio",
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
        )
        Text(
            text = "Image (9:16)",
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        UploadImageContainer(
            onClick = interactionListener::onClickUploadImage
        )
        Spacer(modifier = Modifier.weight(1f))
        NextButton(onClick = interactionListener::onSaveClicked)
    }
}

@Preview
@Composable
fun UploadDukanImageContentPreview() {
    MenaTheme {
        UploadDukanImageContent(
            state = CreateDukanUiState(),
            interactionListener = object : CreateDukanInteractionListener {
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