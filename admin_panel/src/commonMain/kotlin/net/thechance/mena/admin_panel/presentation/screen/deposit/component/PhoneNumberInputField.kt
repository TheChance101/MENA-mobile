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
import net.thechance.mena.admin_panel.presentation.designSystem.theme.emoji
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_arrow_down
import net.thechance.mena.admin_panel.resources.ic_phone
import net.thechance.mena.admin_panel.resources.phone_number
import net.thechance.mena.admin_panel.resources.selected_country
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
                .padding(bottom = 4.dp)
        )
        Box(modifier = modifier) {
            MobileNumberTextField(
                value = phoneNumber,
                onValueChanged = onPhoneChange,
                hint = "",
                leadingIcon = painterResource(Res.drawable.ic_phone),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                visualTransformation = LengthBasedPhoneVisualTransformation(masks = phoneMasks),
                leadingContent = {
                    CountryCodeSelector(
                        countryFlag = selectedCountry.flagEmoji,
                        countryCode = selectedCountry.callingCode,
                        onClick = { expanded = true }
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )

            CountryDropdownMenu(
                expanded = expanded,
                onDismiss = { expanded = false },
                modifier = Modifier
                    .width(328.dp)
                    .heightIn(max = 220.dp)
                    .background(
                        Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(Theme.radius.lg)
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
            .height(48.dp)
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(vertical = 13.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = countryFlag,
            style = Theme.typography.emoji.medium,
            modifier = Modifier.padding(end = 4.dp),
        )

        Text(
            text = countryCode,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(end = 2.dp),
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_down),
            contentDescription = stringResource(Res.string.selected_country),
            tint = Theme.colorScheme.shadePrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

private val phoneMasks = mapOf(
    8 to "##\u00A0###\u00A0###",
    9 to "###\u00A0###\u00A0###",
    10 to "###\u00A0###\u00A0####",
    11 to "####\u00A0####\u00A0###",
)