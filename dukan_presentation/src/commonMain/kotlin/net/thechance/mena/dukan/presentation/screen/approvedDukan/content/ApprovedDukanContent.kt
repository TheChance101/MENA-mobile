package net.thechance.mena.dukan.presentation.screen.approvedDukan.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add_bold
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ApprovedDukanContent(
    state: ApprovedDukanUiState,
    listener: ApprovedDukanInteractionListener,
) {
    OnSystemBackPressed(listener::onBackButtonClicked)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.shelves.isNotEmpty()) {
            ApprovedDukanHeader(
                state = state,
                listener = listener
            )
        }
        if (state.shelves.isEmpty() || state.products.isEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
        }

        ApprovedDukanProducts(
            state = state,
            onProductClick = listener::onProductClick
        )

        Spacer(modifier = Modifier.weight(1f))

        FabButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = Theme.spacing._16, bottom = Theme.spacing._24),
            onClick = listener::onAddShelfClicked,
            painter = painterResource(Res.drawable.ic_add_bold)
        )

    }

    state.snackBarState?.let { snackBarState ->
        SnackBar(
            snackBarUiState = snackBarState,
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
