package net.thechance.mena.identity.presentation.screen.profile.contactUs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.util.animation.shimmerLoading
import sv.lib.squircleshape.SquircleShape

@Composable
fun ContactUsScreenShimmer() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .shimmerLoading(true),
        contentPadding = PaddingValues(
            vertical = Theme.spacing._8,
            horizontal = Theme.spacing._16
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),

        ) {
        item {
            Box(
                modifier = Modifier
                    .padding(bottom = Theme.spacing._4)
                    .height(20.dp)
                    .width(100.dp)
                    .clip(SquircleShape(Theme.radius.sm))
                    .background(Theme.colorScheme.background.surfaceLow)
                    .shimmerLoading(true)
            )
        }
        items(3) { _ ->
            ContactCardShimmer()
        }
    }
}