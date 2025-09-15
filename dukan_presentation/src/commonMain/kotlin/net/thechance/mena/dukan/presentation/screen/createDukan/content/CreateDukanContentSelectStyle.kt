package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Color
import mena.dukan_presentation.generated.resources.`Customize your dukan`
import mena.dukan_presentation.generated.resources.`Pick color and style for dukan`
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.Style
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component.DukanColor
import net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component.DukanStyle
import net.thechance.mena.dukan.presentation.util.styles
import net.thechance.mena.dukan.presentation.util.toDisplayName
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CreateDukanContentSelectStyle(
    listener: CreateDukanInteractionListener,
    state: CreateDukanUiState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Theme.colorScheme.background.surface)
            .padding(start = Theme.spacing._16, end = Theme.spacing._16)
    ) {
        item {
            Text(
                text = stringResource(Res.string.`Customize your dukan`),
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary
            )
        }
        item {
            Text(
                text = stringResource(Res.string.`Pick color and style for dukan`),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
        item {
            Text(
                text = stringResource(Res.string.Color),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
            )
        }
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            ) {
                state.dukanColors.forEach {
                    DukanColor(
                        backgroundColor = Color(it),
                        onClick = { listener.onColorClicked(it) },
                        isSelected = it == state.selectedColor
                    )
                }
            }
        }
        item {
            Text(
                text = stringResource(Res.string.Style),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)) {
                styles.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                        modifier = Modifier.weight(1f)
                    ) {
                        DukanStyle(
                            state = state,
                            orientation = item.orientation,
                            hasImage = item.hasImage,
                            onClick = { listener.onStyleClicked(item.style) },
                            isSelected = state.selectedStyle == item.style
                        )
                        MenaText(
                            text = item.style.toDisplayName(),
                            style = Theme.typography.label.small,
                            color = Theme.colorScheme.shadeSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateDukanContentSelectStylePreview() {
    MenaTheme {
        CreateDukanContentSelectStyle(
            state = CreateDukanUiState(
                selectedStyle = null,
                selectedColor = null
            ),
            listener = object : CreateDukanInteractionListener {
                override fun onButtonClicked() {}
                override fun onBackClicked() {}
                override fun onColorClicked(color: Long) {}
                override fun onStyleClicked(style: Dukan.Style) {}
            }
        )
    }
}