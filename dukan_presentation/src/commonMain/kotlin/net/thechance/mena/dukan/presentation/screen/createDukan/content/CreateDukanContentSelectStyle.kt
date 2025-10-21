package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.color
import mena.dukan_presentation.generated.resources.customize_your_dukan
import mena.dukan_presentation.generated.resources.pick_color_and_style_for_dukan
import mena.dukan_presentation.generated.resources.style
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle.ColorOptionsPlaceholder
import net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle.DukanStyleOptions
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.ColorUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanStyleUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CreateDukanContentSelectStyle(
    listener: CreateDukanInteractionListener,
    state: CreateDukanUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16)
    ) {
        item { CustomizeDukanHeader() }
        item { DukanColorSection(listener, state) }
        item { DukanStyleSection(listener, state) }
    }
}

@Composable
private fun DukanColorSection(
    listener: CreateDukanInteractionListener,
    state: CreateDukanUiState
) {
    Text(
        text = stringResource(Res.string.color),
        style = Theme.typography.title.small,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
    ) {
        state.dukanColors.forEach {
            ColorOptionsPlaceholder(
                backgroundColor = Color(it.color),
                onClick = { listener.onColorClicked(it) },
                isSelected = it.id == state.selectedColor?.id
            )
        }
    }
}

@Composable
private fun DukanStyleSection(
    listener: CreateDukanInteractionListener,
    state: CreateDukanUiState
) {
    Text(
        text = stringResource(Res.string.style),
        style = Theme.typography.title.small,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
    )
    DukanStyleOptions(listener = listener, state = state)
}

@Composable
private fun CustomizeDukanHeader() {
    Text(
        text = stringResource(Res.string.customize_your_dukan),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary
    )
    Text(
        text = stringResource(Res.string.pick_color_and_style_for_dukan),
        style = Theme.typography.body.small,
        color = Theme.colorScheme.shadeSecondary
    )

}

@Preview
@Composable
private fun CreateDukanContentSelectStylePreview() {
    MenaTheme {
        CreateDukanContentSelectStyle(
            state = CreateDukanUiState(
                selectedColor = ColorUiState("1", 0xFFFF0000),
                selectedStyle = CreateDukanUiState.Style.WIDE_IMAGE,
                dukanStyles = listOf(
                    DukanStyleUiState(CreateDukanUiState.Style.WIDE_IMAGE, "Wide Image"),
                    DukanStyleUiState(CreateDukanUiState.Style.SMALL_IMAGE, "Small Image"),
                    DukanStyleUiState(CreateDukanUiState.Style.NO_IMAGE, "No Image"),
                ),
                dukanColors = listOf(
                    ColorUiState("1", 0xFFFF0000),
                    ColorUiState("2", 0xFF00FF00),
                    ColorUiState("3", 0xFF0000FF),
                    ColorUiState("3", 0xFF0BC0FF),
                    ColorUiState("3", 0xFF05FCFF),
                    ColorUiState("3", 0xFF00B8FF),
                    ColorUiState("3", 0xFF0E70FF)
                )
            ),
            listener = PreviewCreateDukanInteractionListener
        )
    }
}