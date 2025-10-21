package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.money_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionAmountRow(
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.padding(end = 4.dp),
            text = amount,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(Res.drawable.img_silver),
            contentDescription = stringResource(Res.string.money_icon)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionAmountRowPreview() {
    MenaTheme {
        TransactionAmountRow(amount = "150.00")
    }
}
