package net.thechance.mena.admin_panel.presentation.screen.deposit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_arrow_down
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun MobileNumberLeadingContent(
    countryCode: String,
    countryFlag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onClick)
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(
                vertical = 13.dp,
                horizontal = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = countryFlag,
            style = Theme.typography.label.medium
        )

        Text(
            text = countryCode,
            style = Theme.typography.label.medium,
            modifier = Modifier.padding(start = 4.dp, end = 2.dp),
            color = Theme.colorScheme.shadePrimary
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_down),
            contentDescription = "arrow down",
            modifier = Modifier.size(16.dp)
        )
    }
}
