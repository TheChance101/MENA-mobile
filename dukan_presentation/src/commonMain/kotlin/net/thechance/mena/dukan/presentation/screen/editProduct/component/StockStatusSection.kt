package net.thechance.mena.dukan.presentation.screen.editProduct.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.product_is_out_of_stock
import net.thechance.mena.designsystem.presentation.component.switches.Switch
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StockStatusSection(
    isOutOfStock: Boolean,
    isEnabled: Boolean,
    onOutOfStockChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Switch(
            isChecked = isOutOfStock,
            onCheckedChange = onOutOfStockChange,
            isEnabled = isEnabled
        )

        Text(
            text = stringResource(Res.string.product_is_out_of_stock),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
        )
    }
}

@Preview
@Composable
private fun StockStatusSectionPreview() {
    MenaTheme {
        StockStatusSection(
            isOutOfStock = false,
            isEnabled = true,
            onOutOfStockChange = {}
        )
    }
}
