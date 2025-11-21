package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.apply_filters
import mena.wallet_presentation.generated.resources.filter_transactions
import mena.wallet_presentation.generated.resources.reset
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.FilterContent
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionFilterState
import net.thechance.mena.wallet.presentation.utils.formatLocalDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.TransactionFilterBottomSheet(
    isVisible: Boolean,
    uiState: TransactionFilterState,
    onDismiss: () -> Unit,
    onClickAddFilter: () -> Unit,
    onResetClicked: () -> Unit,
    onStartDateClicked: () -> Unit,
    onEndDateClicked: () -> Unit,
    onTypeToggled: (FilterType) -> Unit,
    onStatusSelected: (FilterStatus) -> Unit,
    modifier: Modifier = Modifier
) {

    BottomSheet(
        isVisible = isVisible,
        onDismissRequest = onDismiss,
        skipPartiallyExpanded = true,
        modifier = modifier.navigationBarsPadding(),
        stickyFooterContent = {
            StickyFooterContent(
                isApplyButtonEnabled = uiState.isApplyButtonEnabled,
                isLoading = uiState.isApplyButtonLoading,
                onClickAddFilter = onClickAddFilter
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderFilterContent(onResetClicked = onResetClicked)

                FilterContent(
                    selectedTypes = uiState.selectedTypes,
                    selectedStatus = uiState.selectedStatus,
                    startDate = uiState.startDate?.let {
                        formatLocalDate(
                            date = it,
                            outputFormat = "yyyy/MM/dd"
                        )
                    } ?: "",
                    endDate = uiState.endDate?.let {
                        formatLocalDate(
                            date = it,
                            outputFormat = "yyyy/MM/dd"
                        )
                    } ?: "",
                    onTypeSelected = onTypeToggled,
                    onStatusSelected = onStatusSelected,
                    onStartDateClicked = onStartDateClicked,
                    onEndDateClicked = onEndDateClicked
                )
            }
        }
    )
}

@Composable
private fun StickyFooterContent(
    isApplyButtonEnabled: Boolean,
    isLoading: Boolean,
    onClickAddFilter: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .padding(bottom = 24.dp)
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            )
    ) {
        PrimaryButton(
            text = stringResource(Res.string.apply_filters),
            isEnabled = isApplyButtonEnabled,
            isLoading = isLoading,
            onClick = onClickAddFilter,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Composable
private fun HeaderFilterContent(
    onResetClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.filter_transactions),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary
        )
        TextButton(
            text = stringResource(Res.string.reset),
            onClick = onResetClicked
        )
    }
}

@Preview
@Composable
private fun TransactionFilterBottomSheetPreview() {
    MenaTheme {
        Scaffold(
            overlays = {
                bottomSheet(true) {
                    TransactionFilterBottomSheet(
                        isVisible = it,
                        uiState = TransactionFilterState(),
                        onDismiss = {},
                        onResetClicked = {},
                        onClickAddFilter = {},
                        onTypeToggled = {},
                        onStatusSelected = {},
                        onStartDateClicked = {},
                        onEndDateClicked = {}
                    )
                }
            }
        ) {}
    }
}