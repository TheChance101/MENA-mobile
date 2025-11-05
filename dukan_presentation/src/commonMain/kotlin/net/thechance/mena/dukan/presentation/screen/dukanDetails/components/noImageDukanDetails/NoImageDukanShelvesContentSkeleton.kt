package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.SkeletonOverlayShape
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import kotlin.random.Random

@Composable
fun NoImageDukanShelvesContentSkeleton() {
    LazyColumn(
        modifier = Modifier.padding(top = Theme.spacing._16),
        contentPadding = PaddingValues(Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        repeat(6) {
            item {
                ShelfHeaderSkeleton()
            }
            item {
                ShelfProductsSkeleton()
            }
        }
    }
}

@Composable
private fun ShelfHeaderSkeleton() {
    Row(
        modifier = Modifier.padding(horizontal = Theme.spacing._16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonOverlayShape(
            modifier = Modifier
                .height(Theme.spacing._16)
                .width(128.dp)
        )
    }
}

@Composable
private fun ShelfProductsSkeleton() {
    val cardCount = remember { Random.nextInt(5) + 2 }

    Column(
        modifier = Modifier.padding(bottom = Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        repeat(cardCount) {
            LoadingProductCard()
        }
    }
}