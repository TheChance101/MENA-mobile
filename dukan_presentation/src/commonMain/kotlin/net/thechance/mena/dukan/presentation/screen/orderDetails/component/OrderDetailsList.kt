package net.thechance.mena.dukan.presentation.screen.orderDetails.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_clock_time
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OrderDetailsList() {
    Column {
        OrderDateTime()
    }
}

@Composable
private fun OrderDateTime() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ){
        Icon(
            painter = painterResource(Res.drawable.ic_clock_time),
            contentDescription = "Order Date",
            tint = Theme.colorScheme.shadePrimary,
        )
        Text(
            text = "12/04/2025",
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Preview
@Composable
private fun OrderDetailsScreenPreview() {
    MenaTheme {
        OrderDetailsList()
    }
}