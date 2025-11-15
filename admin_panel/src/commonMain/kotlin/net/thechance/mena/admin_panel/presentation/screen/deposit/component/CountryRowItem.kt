package net.thechance.mena.identity.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositScreenState
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
@Composable
internal fun CountryRowItem(
    selectedCountry: DepositScreenState.CountryUiState,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (DepositScreenState.CountryUiState) -> Unit = {}
) {

    val animatedCountryItemColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.background.surfaceHigh
        else Theme.colorScheme.background.surfaceLow
    )


    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = animatedCountryItemColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick(selectedCountry) }
            )
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._12)
    ) {

       Text(
           text=selectedCountry.flagEmoji,
           style = Theme.typography.title.large

       )
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = Theme.spacing._8)
        ) {
            Text(
                text = selectedCountry.name,
                color = Theme.colorScheme.primary.primary,
                style = Theme.typography.title.small,
            )

            Text(
                text = selectedCountry.callingCode,
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
