package net.thechance.mena.faith.presentation.feature.quran.tilwah

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.ReciterItem
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.TilawahTopBar
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
fun Content(
    uiState: TilawahUiState,
    listener: TilawahInteractionListener,
) {
    Scaffold(
        topBar = {
            AppBar(
                title = "Reciters",
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16, vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.arrow_left)
                    )
                },
                onLeadingClick = listener::onBackClick,
                trailingContent = { TilawahTopBar(listener::onSearchClick) }
            )
        }) {
        LazyColumn(
            modifier = Modifier.padding(bottom = Theme.spacing._16)
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