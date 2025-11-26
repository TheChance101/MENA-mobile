package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.filter_transaction_error_description
import mena.wallet_presentation.generated.resources.filter_transaction_error_title
import mena.wallet_presentation.generated.resources.ic_error_dark
import mena.wallet_presentation.generated.resources.img_filter_error
import net.thechance.mena.wallet.presentation.component.StatePlaceholder
import net.thechance.mena.wallet.presentation.navigation.LocalDarkTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterTransactionEmpty(modifier: Modifier = Modifier) {
    val emptyTransactionHistoryIcon =
        if (LocalDarkTheme.current) painterResource(Res.drawable.ic_error_dark)
        else
            painterResource(Res.drawable.img_filter_error)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = emptyTransactionHistoryIcon,
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