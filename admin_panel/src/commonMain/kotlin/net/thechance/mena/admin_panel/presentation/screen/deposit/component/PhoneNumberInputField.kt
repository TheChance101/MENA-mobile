package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.amount
import net.thechance.mena.admin_panel.resources.ic_arrow_down
import net.thechance.mena.admin_panel.resources.ic_phone
import net.thechance.mena.admin_panel.resources.phone_number
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.MobileNumberTextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PhoneNumberInputField(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: DepositScreenState.CountryUiState,
    availableCountries: List<DepositScreenState.CountryUiState>,
    onCountrySelected: (DepositScreenState.CountryUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.phone_number),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Theme.spacing._4)
        )
        Box(modifier = modifier) {
            MobileNumberTextField(
                value = phoneNumber,
                onValueChanged = onPhoneChange,
                hint = "",
                leadingIcon = painterResource(Res.drawable.ic_phone),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingContent = {
                    CountryCodeSelector(
                        countryFlag = selectedCountry.flagEmoji,
                        countryCode = selectedCountry.callingCode,
                        onClick = { expanded = true }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            CountryDropdownMenu(
                expanded = expanded,
                onDismiss = { expanded = false },
                modifier = Modifier
                    .width(328.dp)
                    .heightIn(max = 220.dp)
                    .background(
                        Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(16.dp)
                    ),
                selectedCountry = selectedCountry,
                availableCountries = availableCountries,
                onCountrySelected = {
                    onCountrySelected(it)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun CountryCodeSelector(
    countryFlag: String,
    countryCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onClick)
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = countryFlag,
            style = Theme.typography.body.large,
            modifier = Modifier.padding(end = 1.dp)
        )

        Text(
            text = countryCode,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_down),
            contentDescription = "Select country",
            tint = Theme.colorScheme.shadePrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}