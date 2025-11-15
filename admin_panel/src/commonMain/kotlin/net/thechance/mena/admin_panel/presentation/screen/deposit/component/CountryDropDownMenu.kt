package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.pick_country
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.CountryRowItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun CountryDropdownMenu(
    expanded: Boolean,
    selectedCountry: DepositScreenState.CountryUiState,
    availableCountries: List<DepositScreenState.CountryUiState>,
    onCountrySelected: (DepositScreenState.CountryUiState) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(0.dp, 4.dp),
        properties = PopupProperties(focusable = true),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.pick_country),
                style = Theme.typography.title.small
            )

            availableCountries.forEach { country ->
                CountryRowItem(
                    selectedCountry = country,
                    isSelected = country.callingCode == selectedCountry.callingCode,
                    onClick = {
                        onCountrySelected(country)
                        onDismiss()
                    }
                )
            }
        }
    }
}
