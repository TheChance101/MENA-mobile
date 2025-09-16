package net.thechance.mena.dukan.presentation.screen.createDukan.content.component.dukanstyle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState

@Composable
fun DukanStyleOptions(
    listener: CreateDukanInteractionListener,
    state: CreateDukanUiState
){
    Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)) {
        state.dukanStyles.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                modifier = Modifier.weight(1f)
            ) {
                DukanStyle(
                    state = state,
                    orientation = item.orientation,
                    hasImage = item.hasImage,
                    onClick = { listener.onStyleClicked(item) },
                    isSelected = state.selectedStyle == item
                )
                MenaText(
                    text = item.label,
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.shadeSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}