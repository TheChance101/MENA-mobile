package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun LoadingProductsHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 24.dp)
                .background(
                    color = Theme.colorScheme.background.surfaceHigh,
                    shape = RoundedCornerShape(Theme.radius.md)
                )
        )
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 24.dp)
                .background(
                    color = Theme.colorScheme.background.surfaceHigh,
                    shape = RoundedCornerShape(Theme.radius.md)
                )
        )
    }
}