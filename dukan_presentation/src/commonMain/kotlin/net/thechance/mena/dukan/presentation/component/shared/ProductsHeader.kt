package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.all
import mena.dukan_presentation.generated.resources.arrow_right_icon
import mena.dukan_presentation.generated.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductsHeader(
    shelfName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewAllColor: Color = Theme.colorScheme.primary.primary
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shelfName,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = null
            ) { onClick() },
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.all),
                style = Theme.typography.label.medium,
                color = viewAllColor
            )
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                tint = viewAllColor,
                contentDescription = stringResource(Res.string.arrow_right_icon),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductsHeaderPreview() {
    MenaTheme {
        ProductsHeader(
            shelfName = "Category Name",
            modifier = Modifier.padding(16.dp),
            onClick = {}
        )
    }
}