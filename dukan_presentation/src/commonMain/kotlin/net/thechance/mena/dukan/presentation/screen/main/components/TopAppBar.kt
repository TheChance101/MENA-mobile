package net.thechance.mena.dukan.presentation.screen.main.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    onDukanIconClicked: () -> Unit,
    dukanButtonStatus: MainScreenUiState.DukanStatusUi,
) {
    AppBar(
        title = stringResource(resource = Res.string.dukan),
        modifier = modifier,
        titleColor = Theme.colorScheme.shadePrimary,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._8
        ),
        trailingContent = {
            DukanIconButton(
                dukanButtonStatus = dukanButtonStatus,
                onDukanIconClicked = onDukanIconClicked,
            )
        }
    )
}

@Composable
private fun DukanIconButton(
    dukanButtonStatus: MainScreenUiState.DukanStatusUi,
    onDukanIconClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clip(shape = RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onDukanIconClicked),
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
                    contentDescription = stringResource(resource = Res.string.add_dukan_icon)
                )
            }

            MainScreenUiState.DukanStatusUi.Pending -> {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_dukan),
                    contentDescription = stringResource(resource = Res.string.dukan_icon)
                )
            }

            MainScreenUiState.DukanStatusUi.Approved -> {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_dukan),
                    contentDescription = stringResource(resource = Res.string.dukan_icon)
                )
            }

            MainScreenUiState.DukanStatusUi.Default -> {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_dukan),
                    contentDescription = stringResource(resource = Res.string.dukan_icon)
                )
            }

            MainScreenUiState.DukanStatusUi.Loading -> {
                LoadingDots(modifier = Modifier.size(size = 20.dp))
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
                onDukanIconClicked = {})
        }
    }
}