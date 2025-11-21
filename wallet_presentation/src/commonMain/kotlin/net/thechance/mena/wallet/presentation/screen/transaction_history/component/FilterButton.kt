package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.filter
import mena.wallet_presentation.generated.resources.ic_filter
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterButton(
    activeFilterCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        onClick = onClick,
        containerColor = Theme.colorScheme.brand.brandVariant,
        shape = CircleShape,
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(Res.drawable.ic_filter),
            contentDescription = stringResource(Res.string.filter),
            tint = Theme.colorScheme.primary.primary
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = stringResource(Res.string.filter),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.primary.primary
        )
        FilterCount(activeFilterCount = activeFilterCount)
    }
}

@Composable
@Preview
private fun FilterButtonPreview() {
    FilterButton(
        activeFilterCount = 1,
        onClick = {}
    )
}