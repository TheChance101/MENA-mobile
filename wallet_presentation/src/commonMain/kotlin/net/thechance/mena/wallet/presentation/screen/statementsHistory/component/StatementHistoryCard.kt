package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.amount_with_currency
import mena.wallet_presentation.generated.resources.date_range
import mena.wallet_presentation.generated.resources.ic_clock
import mena.wallet_presentation.generated.resources.inflows
import mena.wallet_presentation.generated.resources.outflows
import mena.wallet_presentation.generated.resources.silvers
import mena.wallet_presentation.generated.resources.transaction_history
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatementHistoryCard(
    startDate: String,
    endDate: String,
    totalInflow: String,
    totalOutflow: String,
    onStatementCardClicked: () -> Unit,
    historyIconOffsetX: Int,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isEditMode) {
                    Modifier
                } else {
                    Modifier.clickable { onStatementCardClicked() }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatementHistoryIcon(
            modifier = Modifier.offset { IntOffset(historyIconOffsetX, 0) }
        )

        StatementHistoryContent(
            startDate = startDate,
            endDate = endDate,
            totalInflow = totalInflow,
            totalOutflow = totalOutflow,
            isEditMode = isEditMode
        )
    }
}

@Composable
private fun StatementHistoryIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(Res.drawable.ic_clock),
        contentDescription = stringResource(Res.string.transaction_history),
        tint = Theme.colorScheme.shadeSecondary,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Theme.colorScheme.primary.onPrimary)
            .padding(12.dp)
    )
}

@Composable
private fun StatementHistoryContent(
    startDate: String,
    endDate: String,
    totalInflow: String,
    totalOutflow: String,
    isEditMode: Boolean = false,
) {
    Column(
        modifier = Modifier.padding(start = if (isEditMode) 4.dp else 0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.date_range, startDate, endDate),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadePrimary
        )
        StatementInOutflowRow(totalInflow, totalOutflow)
    }
}

@Composable
private fun StatementInOutflowRow(totalInflow: String, totalOutflow: String) {
    FlowRow(
        verticalArrangement = Arrangement.Center
    ) {
        FlowItem(
            amount = "+$totalInflow",
            label = stringResource(Res.string.inflows),
            color = Theme.colorScheme.success
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .height(16.dp)
                .width(1.dp)
                .background(Theme.colorScheme.stroke)
        )
        FlowItem(
            amount = "-$totalOutflow",
            label = stringResource(Res.string.outflows),
            color = Theme.colorScheme.error
        )
    }
}

@Composable
private fun FlowItem(
    amount: String,
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(
                Res.string.amount_with_currency,
                amount,
                stringResource(Res.string.silvers)
            ),
            style = Theme.typography.label.extraSmall,
            color = color,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = label,
            style = Theme.typography.label.extraSmall,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Preview
@Composable
private fun StatementItemPreview() {
    MenaTheme {
        StatementHistoryCard(
            startDate = "Jul 23 2025",
            endDate = "Aug 27 2025",
            totalInflow = "2000",
            totalOutflow = "4200",
            historyIconOffsetX = 10,
            onStatementCardClicked = {},
            modifier = Modifier.background(Theme.colorScheme.background.surface)
        )
    }
}