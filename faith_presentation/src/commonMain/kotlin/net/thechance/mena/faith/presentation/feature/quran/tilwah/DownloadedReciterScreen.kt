package net.thechance.mena.faith.presentation.feature.quran.tilwah

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.ReciterItem
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.TilawahTopBar
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun DownloadedReciterScreen(
    viewModel: TilawahViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            TilawahEffect.NavigateBack -> navController.navigateUp()
            TilawahEffect.NavigateToSearch -> navController.navigate(Route.SearchRoute)
        }
    }
    Content(uiState = uiState, listener = viewModel)
}

@OptIn(ExperimentalTime::class)
@Composable
private fun Content(
    uiState: TilawahUiState,
    listener: TilawahInteractionListener,
) {
    Scaffold(
        topBar = {
            TilawahTopBar(
                onSearchClick = listener::onSearchClick,
                onBackClick = listener::onBackClick
            )
        }) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = Theme.spacing._16),
        ) {
            items(uiState.reciters) { reciter ->
                ReciterItem(
                    reciter = reciter.name,
                    recitingType = reciter.recitingType,
                    isDownloaded = reciter.isDownloaded,
                    isSelected = uiState.selectedReciterId == reciter.id,
                    onSelect = {
                        listener.onSelectReciterClick(reciter.id)
                    }
                )
            }
        }
    }
}


@Preview
@Composable
private fun Preview() {
    QuranTheme {
        Content(
            uiState = TilawahUiState(),
            listener = object : TilawahInteractionListener {
                override fun onBackClick() {}
                override fun onSearchClick() {}
                override fun onSelectReciterClick(reciterId: Int) {}
            })
    }
}