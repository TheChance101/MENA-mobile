package net.thechance.mena.dukan.presentation.screen.main.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_dukan_icon
import mena.dukan_presentation.generated.resources.dukan
import mena.dukan_presentation.generated.resources.dukan_button
import mena.dukan_presentation.generated.resources.dukan_icon
import mena.dukan_presentation.generated.resources.ic_add_dukan
import mena.dukan_presentation.generated.resources.ic_dukan
import mena.dukan_presentation.generated.resources.ic_search
import mena.dukan_presentation.generated.resources.search_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.DukanAppBar
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun TopAppBar(
    dukanButtonStatus: MainScreenUiState.DukanStatusUi,
    onDukanIconClicked: () -> Unit,
    onSearchIconClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DukanAppBar(
        title = stringResource(resource = Res.string.dukan),
        modifier = modifier,
        titleColor = Theme.colorScheme.shadePrimary,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._8
        ),
        trailingContent = {
            AppBarOptionContainer(
                onClick = onSearchIconClicked,
            ) {
                SearchIconButton()
            }
            AppBarOptionContainer (
                onClick = onDukanIconClicked,
            ){
                DukanIconButton(
                    dukanButtonStatus = dukanButtonStatus,
                )
            }
        }
    )
}

@Composable
private fun SearchIconButton() {
    Icon(
        painter = painterResource(resource = Res.drawable.ic_search),
        contentDescription = stringResource(resource = Res.string.search_icon),
        modifier = Modifier.size(size = 20.dp),
        tint = Theme.colorScheme.shadePrimary
    )
}

@Composable
private fun DukanIconButton(
    dukanButtonStatus: MainScreenUiState.DukanStatusUi,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(shape = SquircleShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        DukanIcon(dukanStatus = dukanButtonStatus)
    }
}

@Composable
private fun DukanIcon(dukanStatus: MainScreenUiState.DukanStatusUi) {
    AnimatedContent(
        targetState = dukanStatus,
        transitionSpec = { fadeTransitionSpec() },
        label = stringResource(resource = Res.string.dukan_button)
    ) { dukanStatus ->
        when (dukanStatus) {
            MainScreenUiState.DukanStatusUi.None -> {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_add_dukan),
                    contentDescription = stringResource(resource = Res.string.add_dukan_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }

            MainScreenUiState.DukanStatusUi.Pending -> {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_dukan),
                    contentDescription = stringResource(resource = Res.string.dukan_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }

            MainScreenUiState.DukanStatusUi.Approved -> {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_dukan),
                    contentDescription = stringResource(resource = Res.string.dukan_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }

            MainScreenUiState.DukanStatusUi.Default -> {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_dukan),
                    contentDescription = stringResource(resource = Res.string.dukan_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }

            MainScreenUiState.DukanStatusUi.Loading -> {
                DotsProgressIndicator()
            }
        }
    }
}


@Preview
@Composable
private fun TopAppBarPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            TopAppBar(
                dukanButtonStatus = MainScreenUiState.DukanStatusUi.None,
                onDukanIconClicked = {},
                onSearchIconClicked = {}
            )
        }
    }
}