package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.SkeletonOverlayShape
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape


fun LazyGridScope.wideImageProductCardSkeletonGrid(productCount: Int) {
    items(count = productCount) {
        ProductCardSkeleton()
    }
}

@Composable
private fun ProductCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .size(width = 160.dp, height = 240.dp)
            .clip(SquircleShape(Theme.radius.sm))
            .background(Theme.colorScheme.background.surfaceLow)
    ) {
        SkeletonOverlayShape(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .height(176.dp)
                .clip(SquircleShape(Theme.radius.sm))
        )
        ProductCardDetailsSkeleton()
    }


}

@Composable
private fun ProductCardDetailsSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(top = Theme.spacing._12)
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = Theme.spacing._4)
    ) {
        SkeletonOverlayShape(
            modifier = Modifier
                .size(height = Theme.spacing._16, width = 144.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonOverlayShape(
                modifier = Modifier
                    .height(Theme.spacing._24)
                    .width(84.dp)
                    .padding(top = 2.dp)
            )
            SkeletonOverlayShape(
                modifier = Modifier
                    .padding(start = Theme.spacing._4)
                    .clip(CircleShape)
                    .size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Product Card Loading")
@Composable
private fun ProductCardSkeletonGridPreview() {
    MenaTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            wideImageProductCardSkeletonGrid(productCount = 6)
        }
    }
}
