package net.thechance.mena.identity.presentation.screen.countryPicker

import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry

data class CountryPickerUIState(
    val selectedCountry: MenaCountry? = MenaCountry.IRAQ,
    val currentCountry: MenaCountry = MenaCountry.IRAQ,
    val isEnabled: Boolean = false,
    val countries: List<SelectableCountryItemUiState> = defaultCountries
) {
    private companion object {
        val defaultCountries = MenaCountry.entries.map { country ->
            SelectableCountryItemUiState(
                selectableCountry = Selectable(
                    item = country,
                    isSelected = country == MenaCountry.IRAQ // Default selected country
                )
            )
        }
    }
}

data class SelectableCountryItemUiState(
    val selectableCountry: Selectable<MenaCountry> = Selectable(
        item = MenaCountry.EGYPT,
        isSelected = false
    )
)

data class Selectable<T>(
    val item: T,
    val isSelected: Boolean
)


