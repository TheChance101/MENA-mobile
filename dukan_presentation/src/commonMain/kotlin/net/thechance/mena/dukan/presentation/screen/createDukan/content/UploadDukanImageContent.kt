package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.`Dukan image`
import mena.dukan_presentation.generated.resources.ImageSize
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.`Upload dukan image`
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.createDukan.components.UploadImageContainer
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun UploadDukanImageContent(
    state: CreateDukanUiState,
    interactionListener: CreateDukanInteractionListener
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceHigh)
            .padding(horizontal = Theme.spacing._16)
    ) {
        item {
            MenaText(
                text = stringResource(Res.string.`Dukan image`),
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
        item {
            MenaText(
                text = stringResource(Res.string.`Upload dukan image`),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary,
            )
        }
        item {
            MenaText(
                text = stringResource(Res.string.ImageSize),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
            )
        }
        item {
            UploadImageContainer(
                onClick = interactionListener::onClickUploadImage,
                onBottomIconClick = interactionListener::onClickUploadImage,
                showBottomIcon = state.isEditIconVisible
            )
        }
    }
}

@Preview
@Composable
private fun UploadDukanImageContentPreview() {
    MenaTheme {
        UploadDukanImageContent(
            state = CreateDukanUiState(),
            interactionListener = PreviewCreateDukanInteractionListener
        )
    }
}