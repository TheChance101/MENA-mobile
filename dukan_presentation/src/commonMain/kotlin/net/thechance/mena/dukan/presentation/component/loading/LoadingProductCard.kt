package net.thechance.mena.dukan.presentation.component.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun LoadingProductCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colorScheme.background.surfaceLow
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = SquircleShape(Theme.radius.md)
            ).height(104.dp)
            .padding(Theme.spacing._4),
    ) {
        Box(
            modifier = Modifier.background(
                color = Theme.colorScheme.background.surfaceHigh,
                shape = SquircleShape(Theme.radius.md)
            )
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(SquircleShape(Theme.radius.sm))
                    .background(Theme.colorScheme.background.surfaceHigh),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = Theme.spacing._8,
                    top = Theme.spacing._4,
                    end = Theme.spacing._8
                ),
        ) {
            SkeletonOverlayShape(
                modifier = Modifier
                    .height(22.dp)
                    .width(204.dp)
            )
            SkeletonOverlayShape(
                modifier = Modifier
                    .padding(top = Theme.spacing._2)
                    .height(Theme.spacing._16)
                    .fillMaxWidth()
            )
            SkeletonOverlayShape(
                modifier = Modifier
                    .height(Theme.spacing._16)
                    .width(170.dp)

            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Theme.spacing._4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkeletonOverlayShape(
                        modifier = Modifier
                            .height(22.dp)
                            .fillMaxWidth(0.25f)

                    )
                    SkeletonOverlayShape(
                        modifier = Modifier
                            .padding(start = Theme.spacing._4)
                            .clip(CircleShape)
                            .size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun ProductCardSkeletonGridPreview() {
    MenaTheme {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(1)
        ) {
            items(10) {
                LoadingProductCard()
            }
        }
    }
}