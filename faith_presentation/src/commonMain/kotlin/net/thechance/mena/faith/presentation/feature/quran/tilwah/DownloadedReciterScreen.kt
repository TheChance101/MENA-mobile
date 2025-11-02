package net.thechance.mena.faith.presentation.feature.quran.tilwah

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_search
import mena.faith_presentation.generated.resources.ic_tick_double_check
import mena.faith_presentation.generated.resources.search_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun DownloadedReciterScreen(viewModel: TilawahViewModel = koinViewModel()
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
    var selectedReciterId by remember { mutableStateOf<Int?>(null) }

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
            items(uiState.reciter.size) { index ->
                ReciterItem(
                    reciter = uiState.reciter[index],
                    recitingType = uiState.recitingType,
                    isDownloaded = uiState.isDownloaded,
                    isSelected = selectedReciterId == index,
                    onSelect = {
                        selectedReciterId = index
                        listener::onSelectReciterClick
                    }
                )
            }
        }
    }
}

@Composable
private fun TilawahTopBar(onSearchClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(onClick = onSearchClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = stringResource(Res.string.search_icon)
        )
    }
}

@Composable
private fun ReciterItem(
    reciter: String,
    recitingType: String,
    isDownloaded: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = Theme.spacing._16)
            .padding(bottom = Theme.spacing._8)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clickable(onClick = onSelect)
            .padding(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = reciter,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Text(
                    text = recitingType,
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.shadePrimary
                )
                if (isDownloaded) {
                    Icon(
                        painterResource(Res.drawable.ic_tick_double_check),
                        contentDescription = "",
                        modifier = Modifier.size(Theme.spacing._12)
                    )

                    Text(
                        text = "Downloaded",
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.success
                    )
                }
            }
        }

        RadioButton(
            isSelected = isSelected,
            onClick = onSelect
        )
    }
}

@Preview
@Composable
fun Preview() {
    QuranTheme {
        Content(
            uiState = TilawahUiState(
                reciter = listOf("Muhammad Siddiq Al-Minshawi"),
                recitingType = "Teacher - Tajweed",
                isDownloaded = true
            ),
            listener = object : TilawahInteractionListener {
                override fun onBackClick() {}
                override fun onSearchClick() {}
                override fun onSelectReciterClick() {}
            })
    }
}