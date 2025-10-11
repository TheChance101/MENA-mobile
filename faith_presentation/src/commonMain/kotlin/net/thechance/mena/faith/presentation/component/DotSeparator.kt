package net.thechance.mena.faith.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun DotSeparator() {
    Box(
        modifier = Modifier
            .padding(horizontal = Theme.spacing._8)
            .size(3.dp)
            .background(
                color = Theme.colorScheme.shadeTertiary,
                shape = RoundedCornerShape(Theme.radius.full)
            )
    )
}