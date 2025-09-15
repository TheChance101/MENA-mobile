package net.thechance.mena.dukan.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.home.components.TopAppBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen() {
    val viewModel = MainViewModel(dukanRepository = TODO(reason = "should using koin di"))
    val state = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit){
        viewModel.effect.collectLatest{ effect ->
            when(effect){
                MainEffect.NavigateToAddDukanScreen -> { TODO("implement a navigation to add dukan screen") }
                MainEffect.NavigateToPendingDukanScreen -> { TODO("implement a navigation to pending dukan screen") }
            }
        }
    }
    MainContent(
        listener = viewModel,
        state = state.value
    )
}

@Composable
private fun MainContent(
    listener: MainInteractionListener,
    state: MainScreenUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding(),
    ) {
        TopAppBar(
            dukanButtonStatus = state.dukanStatus,
            onAddDukanIconClicked = listener::onDukanButtonClicked,
        )

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
                listener = object : MainInteractionListener {
                    override fun onDukanButtonClicked() {}
                },
                state = MainScreenUiState()
            )
        }
    }
}