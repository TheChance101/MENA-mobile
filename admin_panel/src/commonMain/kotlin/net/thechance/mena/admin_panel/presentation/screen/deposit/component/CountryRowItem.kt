package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositScreenState
import net.thechance.mena.admin_panel.presentation.designSystem.theme.emoji
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun CountryRowItem(
    selectedCountry: DepositScreenState.CountryUiState,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (DepositScreenState.CountryUiState) -> Unit = {}
) {

    val countryItemColor = if (isSelected) Theme.colorScheme.background.surfaceHigh
    else Theme.colorScheme.background.surfaceLow

    val animatedCountryItemColor by animateColorAsState(
        targetValue = countryItemColor
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = animatedCountryItemColor,
                shape = RoundedCornerShape(Theme.radius.lg)
            )
            .clickable(
                onClick = { onClick(selectedCountry) }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Text(
            text = selectedCountry.flagEmoji,
            style = Theme.typography.emoji.medium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = selectedCountry.name,
                color = Theme.colorScheme.primary.primary,
                style = Theme.typography.title.small,
            )

            Text(
                text = "(${selectedCountry.callingCode})",
                color = Theme.colorScheme.shadeSecondary,
                style = Theme.typography.label.small,
            )
        }
        if (isSelected) {
            RadioButton(
                isSelected = isSelected,
                onClick = null
            )
        }
    }
}
