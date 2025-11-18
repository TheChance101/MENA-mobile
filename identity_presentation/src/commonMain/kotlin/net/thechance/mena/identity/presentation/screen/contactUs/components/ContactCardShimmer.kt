package net.thechance.mena.identity.presentation.screen.contactUs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.util.animation.shimmerLoading
import sv.lib.squircleshape.SquircleShape

@Composable
internal fun ContactCardShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SquircleShape(Theme.radius.lg))
            .background(
                Theme.colorScheme.background.surfaceLow,
                SquircleShape(Theme.radius.lg)
            )
            .padding(Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(SquircleShape(Theme.radius.sm))
                .background(
                    Theme.colorScheme.background.surfaceHigh,
                    SquircleShape(Theme.radius.sm)
                )
                .shimmerLoading(true)
        )

        Column(
            modifier = Modifier
                .padding(start = Theme.spacing._8)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(14.dp)
                    .clip(SquircleShape(Theme.radius.sm))
                    .shimmerLoading(true)
            )
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(18.dp)
                    .clip(SquircleShape(Theme.radius.sm))
                    .shimmerLoading(true)
            )
        }
    }
}
