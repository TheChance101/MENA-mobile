package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TransactionDetailsScreen(
    id: Uuid,
    onNavigateBackClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(Theme.colorScheme.background.surface)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Transaction Details Screen\nid = $id",
            style = Theme.typography.title.large,
            color = Theme.colorScheme.shadePrimary
        )
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.colorScheme.brand.brand)
                .clickable(onClick = onNavigateBackClicked)
                .padding(8.dp),
            text = "Navigate Back",
            style = Theme.typography.body.small,
            color = Theme.colorScheme.brand.onBrand,
        )
    }
}