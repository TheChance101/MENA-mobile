package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.amount
import net.thechance.mena.admin_panel.resources.ic_add_money
import net.thechance.mena.admin_panel.resources.ic_coin
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.BasicTextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AmountInputField(
    amount: String,
    onAmountChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.amount),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Theme.spacing._4)
        )
        BasicTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = amount,
            onValueChanged = { newText ->
                onAmountChanged(newText)
            },
            hint = "",
            leadingIcon = painterResource(Res.drawable.ic_add_money),
            showTrailingDivider = true,
            trailingIcon = painterResource(Res.drawable.ic_coin),
            visualTransformation = AmountVisualTransformation()
            )
    }
}