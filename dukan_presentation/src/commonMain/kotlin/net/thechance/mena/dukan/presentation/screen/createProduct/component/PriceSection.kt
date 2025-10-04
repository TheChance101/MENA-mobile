package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_price
import mena.dukan_presentation.generated.resources.price
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.priceSection(
    price: String,
    isTextFieldEnabled: Boolean,
    onPriceChange: (String) -> Unit
) {
    item {
        Text(
            text = stringResource(Res.string.price),
            style = Theme.typography.title.medium,
            modifier = Modifier.padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16)

        )
        TextField(
            modifier = Modifier.padding(top = Theme.spacing._4)
                .height(48.dp)
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