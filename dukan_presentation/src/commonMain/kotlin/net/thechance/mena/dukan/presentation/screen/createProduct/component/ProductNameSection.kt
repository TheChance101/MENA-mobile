package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_product
import mena.dukan_presentation.generated.resources.product_name
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductNameSection(
    productName: String,
    isTextFieldEnabled: Boolean,
    onProductNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier){
        Text(
            text = stringResource(Res.string.product_name),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
        TextField(
            modifier = Modifier
                .padding(top = Theme.spacing._4)
                .padding(horizontal = Theme.spacing._16),
            value = productName,
            onValueChanged = onProductNameChange,
            leadingIcon = painterResource(resource = Res.drawable.ic_product),
            leadingIconTint = Theme.colorScheme.shadePrimary,
            maxCharacters = 50,
            enabled = isTextFieldEnabled,
            hint = ""
        )
    }
}

@Preview
@Composable
private fun ProductNameSectionPreview() {
    MenaTheme {
        ProductNameSection(
            productName = "T-Shirt",
            isTextFieldEnabled = true,
            onProductNameChange = {}
        )
    }
}