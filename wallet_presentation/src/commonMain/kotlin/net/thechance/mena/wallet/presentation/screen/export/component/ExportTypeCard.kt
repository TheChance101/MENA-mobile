package net.thechance.mena.wallet.presentation.screen.export.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.all_transactions
import mena.wallet_presentation.generated.resources.background_color_animation
import mena.wallet_presentation.generated.resources.custom_filtering
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ExportTypeCard(
    cardTitle: String,
    onCardSelected: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
) {
    val targetColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.brand.brandVariant else Theme.colorScheme.background.surfaceLow,
    )

    val backgroundColor by animateColorAsState(
        targetValue = targetColor,
        label = stringResource(Res.string.background_color_animation)
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable(enabled = isEnabled, onClick = onCardSelected)
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .border(
                if (isSelected) 1.dp else 0.dp,
                color = Theme.colorScheme.stroke,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .padding(vertical = 19.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(
            isSelected = isSelected,
            onClick = onCardSelected,
            isEnabled = isEnabled
        )
        Text(
            text = cardTitle,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.body.small,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@Preview
private fun SelectCardPreview() {
    MenaTheme {
        ExportTypeCard(
            cardTitle = stringResource(Res.string.all_transactions),
            isSelected = true,
            onCardSelected = {}
        )
    }
}

@Composable
@Preview
private fun SelectCardFalsePreview() {
    MenaTheme {
        ExportTypeCard(
            cardTitle = stringResource(Res.string.custom_filtering),
            isSelected = false,
            onCardSelected = {}
        )
    }
}
