package net.thechance.mena.admin_panel.presentation.screen.deposit.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_phone
import net.thechance.mena.admin_panel.resources.phone_number
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.MobileNumberTextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PhoneNumberInputField(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    countryCode: String,
    countryFlag: String,
    onClickCountry: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(Res.string.phone_number)
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Theme.spacing._4)
        )
        PhoneNumberInput(
            countryCode = countryCode,
            countryFlag = countryFlag,
            onCountryClick = onClickCountry,
            phoneNumber = phoneNumber,
            onPhoneChange = onPhoneChange
        )
    }
}

@Composable
private fun PhoneNumberInput(
    countryCode: String,
    countryFlag: String,
    onCountryClick: () -> Unit,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    phoneNumberFilter: (String) -> String = { it.filter { char -> char.isDigit() } }
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MobileNumberLeadingContent(
            countryCode = countryCode,
            countryFlag= countryFlag,
            onClick = onCountryClick
        )

        MobileNumberTextField(
            value = phoneNumber,
            leadingIcon = painterResource(Res.drawable.ic_phone),
            onValueChanged = { newValue ->
                val filtered = phoneNumberFilter(newValue)
                onPhoneChange(filtered)
            },
            hint = "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.weight(1f)
        )
    }
}
