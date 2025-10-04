package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_product
import mena.dukan_presentation.generated.resources.product_name
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


fun LazyListScope.productNameSection(
    productName: String,
    isTextFieldEnabled: Boolean,
    onProductNameChange: (String) -> Unit
) {
    item {
        Text(
            text = stringResource(Res.string.product_name),
            style = Theme.typography.title.medium,
            modifier = Modifier.padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16)


        )
        TextField(
            modifier = Modifier
                .padding(top = Theme.spacing._4)
                .padding(horizontal = Theme.spacing._16)
                .height(48.dp),
            value = productName,
            onValueChanged = onProductNameChange,
            leadingIcon = painterResource(resource = Res.drawable.ic_product),
            leadingIconTint = Theme.colorScheme.shadePrimary,
            enabled = isTextFieldEnabled,
            hint = ""
        )
    }
}