package net.thechance.mena.identity.presentation.screen.countryPicker

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.components.CountrySelectableRowItem
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.CountryPicker(
    isVisible: Boolean,
    currentCountry: MenaCountry,
    onDismiss: () -> Unit,
    onClickConfirm: (MenaCountry) -> Unit,
    modifier: Modifier = Modifier
) {
    val state: CountryPickerUIState by remember { mutableStateOf(CountryPickerUIState()) }
    var selectedCountry by remember { mutableStateOf(currentCountry) }

    BottomSheet(
        isVisible = isVisible,
        onDismissRequest = onDismiss,
        modifier = modifier
            .navigationBarsPadding(),
        stickyFooterContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colorScheme.background.surface)
                    .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._12)
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.confirm),
                    isEnabled = currentCountry != selectedCountry,
                    onClick = { onClickConfirm(selectedCountry) },
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
                modifier = Modifier.padding(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._24
                )
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 72.dp),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                modifier = Modifier.padding(horizontal = Theme.spacing._16)
            ) {
                items(
                    items = state.countries,
                    key = { it.selectableCountry.item.callingCode }
                ) { country ->
                    CountrySelectableRowItem(
                        selectedCountry = country.selectableCountry.item,
                        isSelected = country.selectableCountry.item == selectedCountry,
                        onClick = { selectedCountry = country.selectableCountry.item },
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
                        isVisible = it,
                        onDismiss = {},
                        onClickConfirm = {},
                        currentCountry = MenaCountry.IRAQ,
                    )
                }
            }
        ) {}
    }
}
