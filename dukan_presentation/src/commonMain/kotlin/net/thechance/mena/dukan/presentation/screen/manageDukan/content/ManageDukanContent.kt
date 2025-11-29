package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.foundation.layout.Box
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
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.DeactivatedDukanScreen
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanAppBar
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanSnackbar
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.manageDukanDialog
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ManageDukanContent(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener,
) {
    OnSystemBackPressed(listener::onBackClicked)

    Scaffold(
        overlays = { manageDukanDialog(state, listener) },
        topBar = { ManageDukanAppBar(listener) },
        snakeBar = { ManageDukanSnackbar(state, listener) }
    ) {
        when (state.activation.activationStatus) {
            ManageDukanUiState.ActivationStatus.ACTIVATED,
            ManageDukanUiState.ActivationStatus.ONHOLD -> ManageDukanScreenContent(state, listener)
            ManageDukanUiState.ActivationStatus.DEACTIVATED -> DeactivatedDukanScreen(state.activation.reason)
        }
    }
}

@Composable
private fun ManageDukanScreenContent(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            ManageDukanHeader(
                state = state,
                listener = listener
            )

            if (state.shelves.isEmpty()) Spacer(modifier = Modifier.weight(1f))

            ManageDukanProducts(
                state = state,
                onEditProductClicked = listener::onEditProductClicked
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        FabButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Theme.spacing._16, bottom = Theme.spacing._24),
            onClick = listener::onAddShelfClicked,
            painter = painterResource(Res.drawable.ic_add_bold)
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun ManageDukanContentPreview() {
    MenaTheme {
        ManageDukanContent(
            state = ManageDukanUiState(),
            listener = PreviewManageDukanInteractionListener,
        )
    }
}