package net.thechance.mena.identity.presentation.bottomSheet.countryPicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirm
import mena.identity_presentation.generated.resources.pick_your_country
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.CountrySelectableRowItem
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.CountryPicker(
    isEnabled: Boolean,
    countries: List<SelectableCountryItemUiState>,
    onSelectCountryItem: (MenaCountry) -> Unit,
    onDismiss: () -> Unit,
    onClickConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
            .navigationBarsPadding(),
        stickyFooterContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colorScheme.background.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.confirm),
                    isEnabled = isEnabled,
                    onClick = onClickConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        },
        sheetContent = {

            Text(
                text = stringResource(Res.string.pick_your_country),
                color = Theme.colorScheme.shadePrimary,
                style = Theme.typography.title.small,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 72.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(
                    items = countries,
                    key = { it.selectableCountry.item.callingCode }
                ) { country ->
                    CountrySelectableRowItem(
                        selectedCountry = country.selectableCountry.item,
                        isSelected = country.selectableCountry.isSelected,
                        onClick = onSelectCountryItem,
                    )
                }
            }
        }
    )
}


@Preview
@Composable
private fun CountryPickerPreview() {
    MenaTheme {
        Scaffold(
            overlays = {
                bottomSheet(true) {
                    CountryPicker(
                        isEnabled = true,
                        countries = List(18) {
                            SelectableCountryItemUiState(
                                selectableCountry = Selectable(
                                    item = MenaCountry.PALESTINE,
                                    isSelected = it == 0
                                )
                            )
                        },
                        onSelectCountryItem = {},
                        onDismiss = {},
                        onClickConfirm = {}
                    )
                }
            }
        ) {}
    }
}
