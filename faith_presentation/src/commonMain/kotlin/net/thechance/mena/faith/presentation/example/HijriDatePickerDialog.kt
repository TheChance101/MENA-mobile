package net.thechance.mena.faith.presentation.example

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.appBar.HomeAppBar
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.HijriDatePickerDialog(
    isVisible: Boolean,
    selectedDay: Int,
    selectedMonth: Int,
    selectedYear: Int,
    onDismiss: () -> Unit,
    onDateSelected: (day: Int, month: Int, year: Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Select Hijri Date",
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    minYear: Int = 1343,
    maxYear: Int = 1500,
    useShortMonthNames: Boolean? = null,
) {
    var tempDay by remember(selectedDay) { mutableStateOf(selectedDay) }
    var tempMonth by remember(selectedMonth) { mutableStateOf(selectedMonth) }
    var tempYear by remember(selectedYear) { mutableStateOf(selectedYear) }

    BasicDialog(
        isVisible = isVisible,
        onDismiss = onDismiss,
        hasDismissButton = true,
        dismissOnBackPress = true,
        dismissOnClickOutside = false,
        contentColor = Theme.colorScheme.background.surfaceLow,
        scrimColor = Theme.colorScheme.primary.primary.copy(0.55f),
        onCancelClick = onDismiss,
        actionButtons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cancelText,
                    color = Theme.colorScheme.shadeSecondary,
                    style = Theme.typography.label.medium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable(
                            onClick = onDismiss,
                            role = Role.Button,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )

                Text(
                    text = confirmText,
                    color = Theme.colorScheme.primary.primary,
                    style = Theme.typography.label.medium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable(
                            onClick = {
                                onDateSelected(tempDay, tempMonth, tempYear)
                                onDismiss()
                            },
                            role = Role.Button,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.primary.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            HijriDatePicker(
                selectedDay = tempDay,
                selectedMonth = tempMonth,
                selectedYear = tempYear,
                minYear = minYear,
                maxYear = maxYear,
                useShortMonthNames = useShortMonthNames,
                onDateChange = { day, month, year ->
                    tempDay = day
                    tempMonth = month
                    tempYear = year
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun HijriDatePickerDialogPreview() {
    MenaTheme {
        var showDialog by remember { mutableStateOf(true) }

        Scaffold(
            topBar = { HomeAppBar("Preview") },
            overlays = {
                dialog(isVisible = showDialog) {
                    HijriDatePickerDialog(
                        isVisible = showDialog,
                        selectedDay = 15,
                        selectedMonth = 9,
                        selectedYear = 1446,
                        onDismiss = { showDialog = false },
                        onDateSelected = { _, _, _ -> showDialog = false },
                        title = "Select Hijri Date",
                        confirmText = "Confirm",
                        cancelText = "Cancel",
                        modifier = Modifier.width(400.dp)
                    )
                }
            }
        ) {
            Text(
                text = "Click to show dialog",
                style = Theme.typography.title.medium,
                modifier = Modifier
                    .clickable { showDialog = true }
                    .padding(24.dp)
            )
        }
    }
}
