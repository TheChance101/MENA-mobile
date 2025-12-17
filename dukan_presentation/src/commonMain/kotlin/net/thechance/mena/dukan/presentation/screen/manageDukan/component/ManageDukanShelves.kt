package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.approved_dukan
import mena.dukan_presentation.generated.resources.approved_dukan_dark
import mena.dukan_presentation.generated.resources.dukan_approved_body
import mena.dukan_presentation.generated.resources.dukan_approved_header
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingHorizontalList
import net.thechance.mena.dukan.presentation.component.state.ImageWithTextContainer
import net.thechance.mena.dukan.presentation.navigation.LocalDarkTheme
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageDukanNoShelvesContent() {
    val isDark = LocalDarkTheme.current
    val icon = if(isDark) Res.drawable.approved_dukan_dark else Res.drawable.approved_dukan


    ImageWithTextContainer(
        foregroundImageRes = icon,
        header = {
            Text(
                text = stringResource(Res.string.dukan_approved_header),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center
            )
        },
        bodyText = stringResource(Res.string.dukan_approved_body)
    )
}

@Composable
fun ManageDukanLoadedShelvesRow(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            items = state.shelves,
            key = { it.id }
        ) { item ->
            Chip(
                text = item.name,
                isSelected = listener.isShelfSelected(item),
                onClick = { listener.onShelfSelected(item) },
            )
        }
    }
}

@Composable
fun ManageDukanLoadingShelvesRow() {
    LoadingHorizontalList(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
    ) {
        Chip(
            text = "             ",
            isSelected = false,
            isEnabled = false,
            onClick = { }
        )
    }
}