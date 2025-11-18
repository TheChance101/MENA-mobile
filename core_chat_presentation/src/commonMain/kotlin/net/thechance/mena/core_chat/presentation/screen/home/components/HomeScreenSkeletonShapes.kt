package net.thechance.mena.core_chat.presentation.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.core_chat.presentation.components.SkeletonShape
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun BalanceSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
    ) {
        SkeletonShape(
            modifier = Modifier
                .size(height = Theme.spacing._16, width = 52.dp)
        )
        SkeletonShape(
            modifier = Modifier
                .size(Theme.spacing._24)
                .clip(CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherAndNextPrayerCardSkeleton() {
    SkeletonShape(
        modifier = Modifier.fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(Theme.radius.lg))
    )
}

@Preview(showBackground = true)
@Composable
fun ChatSummaryListSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
    ) {
        SkeletonShape(
            modifier = Modifier
                .size(height = 24.dp, width = 167.dp)
        )
        repeat(10) {
            ChatSummaryItemSkeleton(modifier = Modifier.padding(bottom = Theme.spacing._4))
        }
    }
}

@Composable
private fun ChatSummaryItemSkeleton(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        SkeletonShape(
            modifier = Modifier
                .padding(end = Theme.spacing._8)
                .size(48.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 39.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SkeletonShape(
                modifier = Modifier
                    .height(22.dp)
                    .fillMaxWidth()
            )
            SkeletonShape(
                modifier = Modifier
                    .height(22.dp)
                    .fillMaxWidth()
            )
        }
        SkeletonShape(
            modifier = Modifier
                .size(height = Theme.spacing._16, width = 46.dp)
        )
    }
}