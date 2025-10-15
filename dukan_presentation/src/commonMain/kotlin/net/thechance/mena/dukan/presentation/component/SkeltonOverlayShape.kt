package net.thechance.mena.dukan.presentation.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SkeletonOverlayShape(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000,
    background: Color = Theme.colorScheme.background.surfaceHigh
) {
    val transition = rememberInfiniteTransition()
    val alphaAnim by transition.animateFloat(
        initialValue = 0.5f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = modifier
            .background(color = background)
            .alpha(alphaAnim)
    )
}

@Preview(showBackground = true)
@Composable
private fun SkeletonLayoutPreview() {
    MenaTheme {
        FadeSkeletonItem()
    }
}

@Composable
private fun FadeSkeletonItem() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        AppBarSkeleton()
        SkeletonOverlayShape(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(92.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        SkeletonOverlayShape(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(height = 24.dp, width = 167.dp)
        )
        ProductsListSkeleton()
    }
}

@Composable
private fun AppBarSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "MENA",
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        SkeletonOverlayShape(
            modifier = Modifier
                .padding(start = 8.dp, end = 4.dp)
                .size(height = 16.dp, width = 52.dp)
        )
        SkeletonOverlayShape(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun ProductsListSkeleton() {
    LazyColumn(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(10) {
            ProductItemSkeleton()
        }

    }
}

@Composable
private fun ProductItemSkeleton() {
    Row() {
        SkeletonOverlayShape(
            modifier = Modifier
                .padding(end = 8.dp)
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
            SkeletonOverlayShape(
                modifier = Modifier
                    .height(22.dp)
                    .fillMaxWidth()
            )
            SkeletonOverlayShape(
                modifier = Modifier
                    .height(22.dp)
                    .fillMaxWidth()
            )
        }
        SkeletonOverlayShape(
            modifier = Modifier
                .size(height = 16.dp, width = 46.dp)
        )
    }
}