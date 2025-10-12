package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_filter_error
import mena.wallet_presentation.generated.resources.no_statements_description
import mena.wallet_presentation.generated.resources.no_statements_title
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.component.StatePlaceholder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyStatementsHistory(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.img_filter_error),
            title = stringResource(Res.string.no_statements_title),
            description = stringResource(Res.string.no_statements_description),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Preview
@Composable
private fun EmptyStatementsHistoryPreview() {
    MenaTheme {
        EmptyStatementsHistory()
    }
}