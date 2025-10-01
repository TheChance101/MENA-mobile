package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import net.thechance.mena.wallet.presentation.component.filter.FilterContent
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionFilterState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.TransactionFilterBottomSheet(
    uiState: TransactionFilterState,
    onDismiss: () -> Unit,
    onClickAddFilter: () -> Unit,
    onResetClicked: () -> Unit,
    onTypeToggled: (FilterType) -> Unit,
    onStatusSelected: (FilterStatus) -> Unit,
    onFromClick: () -> Unit,
    onToClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
            .navigationBarsPadding(),
        stickyFooterContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colorScheme.background.surface)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.apply_filters),
                    isEnabled = uiState.hasActiveFilters,
                    isLoading = uiState.isLoading,
                    onClick = onClickAddFilter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        },
        sheetContent = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.filter_transactions),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    text = stringResource(Res.string.reset),
                    onClick = onResetClicked
                )
            }

            FilterContent(
                selectedTypes = uiState.selectedTypes,
                selectedStatus = uiState.selectedStatus,
                fromDate = uiState.fromDate?.toString() ?: "",
                toDate = uiState.toDate?.toString() ?: "",
                onTypeSelected = onTypeToggled,
                onStatusSelected = onStatusSelected,
                onFromClick = onFromClick,
                onToClick = onToClick
            )

        }
    )
}

@Preview
@Composable
private fun TransactionFilterBottomSheetPreview() {
    MenaTheme {
        Scaffold(
            overlays = {
                bottomSheet(true) {
                    TransactionFilterBottomSheet(
                        uiState = TransactionFilterState(),
                        onDismiss = {},
                        onResetClicked = {},
                        onClickAddFilter = {},
                        onToClick = {},
                        onFromClick = {},
                        onTypeToggled = {},
                        onStatusSelected = {}
                    )
                }
            }
        ) {}
    }
}