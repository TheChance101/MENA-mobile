package net.thechance.mena.dukan.presentation.screen.CreateDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
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
    //   val navController = LocalNavController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceHigh)
            .padding(horizontal = Theme.spacing._16)

    ) {
        MenaText(
            text = "Dukan image",
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = 20.dp)
        )
        MenaText(
            text = "Upload dukan image, it should be rectangle 9:16 aspect ratio",
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
        )
        MenaText(
            text = "Image (9:16)",
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = Theme.spacing._16)
        )
        Spacer(modifier = Modifier.padding(Theme.spacing._4))
        UploadImageContainer(
            onClick = interactionListener::onClickUploadImage,
            onBottomIconClick = interactionListener::onClickUploadImage,
            showBottomIcon = state.isEditIconVisible
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun UploadDukanImageContentPreview() {
    MenaTheme {
        UploadDukanImageContent(
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