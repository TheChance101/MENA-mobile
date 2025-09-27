package net.thechance.mena.dukan.presentation.screen.approvedDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_add_bold
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.my_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ApprovedDukanContent(
    state: ApprovedDukanUiState,
    listener: ApprovedDukanInteractionListener
) {
    OnSystemBackPressed(listener::onBackButtonClicked)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .statusBarsPadding()
    ) {
        AppBar(
            title = stringResource(Res.string.my_dukan),
            onLeadingClick = listener::onBackButtonClicked,
            contentPadding = PaddingValues(
                horizontal = Theme.spacing._16,
                vertical = Theme.spacing._8
            ),
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = stringResource(Res.string.back_arrow),
                    tint = Theme.colorScheme.shadePrimary
                )
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
        ) {
            ApprovedDukanHeader(
                state = state,
                listener = listener
            )

            ApprovedDukanProducts(
                state = state,
                onProductClick = listener::onProductClick
            )
        }

        FabButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Theme.spacing._16, bottom = Theme.spacing._24),
            onClick = listener::onAddShelfClicked,
            painter = painterResource(Res.drawable.ic_add_bold)
        )
    }

    state.snackBarMessage?.let { snackBarMessage ->
        SnackBar(
            snackBarUiState = SnackBarUiState(
                snackBarType = SnackBarType.SUCCESS,
                message = stringResource(Res.string.add_shelf_successfully)
            ),
            isVisible = true,
            onDismiss = listener::onDismissSnackBar
        )
    }
}

@Preview
@Composable
private fun ApprovedDukanContentPreview() {
    MenaTheme {
        ApprovedDukanContent(
            state = ApprovedDukanUiState(),
            listener = PreviewApprovedDukanInteractionListener
        )
    }
}
