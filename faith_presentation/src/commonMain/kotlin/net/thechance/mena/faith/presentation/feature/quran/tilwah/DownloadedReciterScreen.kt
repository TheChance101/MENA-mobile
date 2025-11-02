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
fun DownloadedReciterScreen(viewModel: TilawahViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            TilawahEffect.NavigateBack -> navController.navigateUp()
            TilawahEffect.NavigateToSearch -> navController.navigate(Route.SearchRoute)
        }
    }
    Content(listener = viewModel)
}

@OptIn(ExperimentalTime::class)
@Composable
fun Content(listener: TilawahInteractionListener) {
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
        LazyColumn {
            items(4) {
                ReciterItem(onSelect = listener::onSelectReciterClick)
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
    reciter: String = "Abdul Basit Abdul Samad",
    recitingType: String = "Tajweed - Mujawwad",
    isDownloaded: Boolean = true,
    isSelected: Boolean = false,
    onSelect: () -> Unit
) {
    RadioButton(
        isSelected = isSelected,
        onClick = onSelect
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = Theme.spacing._16)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
    ) {
        Text(
            text = reciter,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(start = Theme.spacing._8, top = 9.dp)
        )
        Row(
            modifier = Modifier.padding(start = Theme.spacing._8, bottom = 9.dp),
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
}

@Preview
@Composable
fun Preview() {
    QuranTheme {
        Content(listener = object : TilawahInteractionListener {
            override fun onBackClick() {}
            override fun onSearchClick() {}
            override fun onSelectReciterClick() {}
        })
    }
}
