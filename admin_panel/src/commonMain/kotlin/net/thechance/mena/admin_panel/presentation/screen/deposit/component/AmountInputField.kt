package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.designsystem.presentation.component.textField.BasicTextField

@Composable
internal fun AmountInputField(
    amount: Double,
    onAmountChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = amount.toString(),
        onValueChanged = { onAmountChanged(it) },
        hint = "",
        leadingIconTint = Res.drawable.ic_silver_icon,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

        )
}