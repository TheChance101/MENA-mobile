package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.pick_country
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
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
    val scrollState = rememberScrollState()
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.pick_country),
                style = Theme.typography.title.small,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(5.dp)
                        .fillMaxHeight()
                        .padding(end = 2.dp),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }
    }
}