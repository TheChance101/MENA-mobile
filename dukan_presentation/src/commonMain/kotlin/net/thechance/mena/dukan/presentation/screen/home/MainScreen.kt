package net.thechance.mena.dukan.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.home.components.TopAppBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen() {
    val viewModel = MainViewModel(dukanRepository = TODO(reason = "should using koin di"))
    val state = viewModel.state.collectAsStateWithLifecycle()
    MainContent(
        mainInteractionListener = viewModel,
        state = state.value
    )
}

@Composable
private fun MainContent(
    mainInteractionListener: MainInteractionListener,
    state: MainScreenUiState
) {
    Column(
        modifier = Modifier
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center
    ) {
        TopAppBar(
            isUserHasDukan = state.isUserHasDukan,
            onAddDukanIconClicked = mainInteractionListener::onAddDukanIconClicked,
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            MainContent(
                mainInteractionListener = object : MainInteractionListener {
                    override fun onAddDukanIconClicked() {}
                },
                state = MainScreenUiState()
            )
        }
    }
}