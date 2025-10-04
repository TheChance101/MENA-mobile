package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.filter_transaction_error_description
import mena.wallet_presentation.generated.resources.filter_transaction_error_title
import mena.wallet_presentation.generated.resources.img_filter_error
import net.thechance.mena.wallet.presentation.component.StatePlaceholder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterTransactionEmpty(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.img_filter_error),
            title = stringResource(Res.string.filter_transaction_error_title),
            description = stringResource(Res.string.filter_transaction_error_description),
            modifier = Modifier.wrapContentSize().align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun FilterTransactionErrorPreview() {
    FilterTransactionEmpty()
}