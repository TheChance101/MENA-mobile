package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_price
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PriceSection(
    title: String,
    price: String,
    isTextFieldEnabled: Boolean,
    onPriceChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = title,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
        TextField(
            modifier = Modifier.padding(top = Theme.spacing._4)
                .padding(horizontal = Theme.spacing._16),
            value = price,
            onValueChanged = onPriceChange,
            leadingIcon = painterResource(resource = Res.drawable.ic_price),
            leadingIconTint = Theme.colorScheme.shadePrimary,
            trailingIcon = painterResource(Res.drawable.silver_tc),
            enabled = isTextFieldEnabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            hint = "",
        )
    }
}

@Preview
@Composable
private fun PriceSectionPreview() {
    MenaTheme {
        PriceSection(
            title = "Price",
            price = "100",
            isTextFieldEnabled = true,
            onPriceChange = {}
        )
    }
}