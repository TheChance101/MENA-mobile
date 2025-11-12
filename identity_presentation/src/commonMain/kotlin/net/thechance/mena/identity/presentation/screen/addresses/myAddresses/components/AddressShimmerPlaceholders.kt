package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddressShimmerPlaceholders(
    modifier: Modifier = Modifier,
    itemsCount: Int = 4
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
        contentPadding = PaddingValues(bottom = Theme.spacing._16)
    ) {
        items(itemsCount) {
            AddressCardShimmer()
        }
    }
}

@Preview
@Composable
private fun PreviewAddressShimmerPlaceholders() {
    MenaTheme {
        AddressShimmerPlaceholders()
    }
}