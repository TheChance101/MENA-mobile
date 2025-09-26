package net.thechance.mena.identity.presentation.bottomSheet.countryPicker

import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry

fun List<SelectableCountryItemUiState>.selectByCountry(country: MenaCountry): List<SelectableCountryItemUiState> {
    return this.map { selectableCountry ->
        selectableCountry.copy(
            selectableCountry = Selectable(
                item = selectableCountry.selectableCountry.item,
                isSelected = selectableCountry.selectableCountry.item == country
            )
        )
    }
}
