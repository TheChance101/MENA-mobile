package net.thechance.mena.faith.presentation.feature.quran.sur

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SurScreen(
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSurahClick: (id: Int) -> Unit,
    viewModel: SurViewModel = SurViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val effect by viewModel.effect.collectAsState(initial = null)

    effect?.let { currentEffect ->
        when (currentEffect) {
            is SurEffect.BackNavigation -> onBackClick()
            is SurEffect.BookMarkNavigation -> onBookmarkClick()
            is SurEffect.SurahDetailsNavigation -> onSurahClick(currentEffect.surahId)
        }
    }

    Content(
        uiState = state,
        contract = viewModel
    )
}

@Composable
private fun Content(
    uiState: SurUiState,
    contract: SurContract,
) {

}
