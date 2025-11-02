package net.thechance.mena.identity.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
internal fun CountrySelectableRowItem(
    selectedCountry: MenaCountry,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (MenaCountry) -> Unit = {}
) {

    val animatedCountryItemColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.background.surfaceHigh
        else Theme.colorScheme.background.surfaceLow
    )

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = animatedCountryItemColor,
                shape = SquircleShape(Theme.radius.lg)
            )
            .clip(SquircleShape(Theme.radius.lg))
            .clickable(
                enabled = !isSelected,
                onClick = {
                    onClick(selectedCountry)
                },
                indication = rippleIndication(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._12)
    ) {

        FlagImage(painterResource(selectedCountry.flagImage))
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = Theme.spacing._8)
        ) {
            Text(
                text = stringResource(selectedCountry.countryNameRes),
                color = Theme.colorScheme.primary.primary,
                style = Theme.typography.title.small,
            )

            Text(
                text = "(${selectedCountry.getFormatedCountryCode(isRtl)})",
                color = Theme.colorScheme.shadeSecondary,
                style = Theme.typography.label.small,
            )
        }

        RadioButton(
            isSelected = isSelected,
            onClick = null
        )
    }
}

@Preview
@Composable
private fun CountrySelectableRowItemPreview() {
    MenaTheme {
        CountrySelectableRowItem(
            selectedCountry = MenaCountry.PALESTINE,
            isSelected = true,
            onClick = {}
        )
    }

}