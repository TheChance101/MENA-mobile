package net.thechance.mena.faith.presentation.feature.prayertime.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.clear_selection
import mena.faith_presentation.generated.resources.ok
import mena.faith_presentation.generated.resources.select_date
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.components.IslamicDatePicker
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.IslamicDatePickerDialog(
    isVisible: Boolean,
    islamicDatePickerUiState: PrayerTimeUiState.IslamicDatePickerUiState,
    onDateChange: (day: Int, month: Int, year: Int) -> Unit,
    onConfirmDateClick: () -> Unit,
    onClearDateClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BasicDialog(
        onDismiss = onDismiss,
        onCancelClick = onDismiss,
        isVisible = isVisible,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._24,
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.select_date),
                style = Theme.typography.title.medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            IslamicDatePicker(
                selectedDate = islamicDatePickerUiState.selectedIslamicDate,
                onDateChange = onDateChange,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.spacing._12)
                    .padding(horizontal = Theme.spacing._12),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.clear_selection),
                    style = Theme.typography.label.medium,
                    color = if (islamicDatePickerUiState.isClearDateActive) Theme.colorScheme.primary.primary else Theme.colorScheme.disabled,
                    modifier = Modifier
                        .clickable(
                            enabled = islamicDatePickerUiState.isClearDateActive,
                            onClick = onClearDateClick,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )

                Text(
                    text = stringResource(Res.string.ok),
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.primary.primary,
                    modifier = Modifier
                        .clickable(
                            onClick = onConfirmDateClick,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
fun IslamicDatePickerDialogPreview() {
    MenaTheme {
        QuranTheme {
            Scaffold(
                overlays = {
                    dialog(isVisible = true) {
                        IslamicDatePickerDialog(
                            isVisible = true,
                            islamicDatePickerUiState = PrayerTimeUiState.IslamicDatePickerUiState(),
                            onDateChange = { _, _, _ -> },
                            onConfirmDateClick = {},
                            onClearDateClick = {},
                            onDismiss = {}
                        )
                    }
                }
            ) {
                Text(
                    text = "Test",
                    style = Theme.typography.title.medium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}
