package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterCount(activeFilterCount: Int) {
    if (activeFilterCount != 0) {
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.brand.brand),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$activeFilterCount",
                style = Theme.typography.label.small,
                color = Theme.colorScheme.primary.onPrimary
            )
        }
    }
}

@Composable
@Preview
private fun FilterCountPreview(){
    FilterCount(
        activeFilterCount = 1
    )
}