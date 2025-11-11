package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.util.animation.shimmerLoading
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddressCardShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(Theme.radius.lg))
            .shimmerLoading(isLoading = true)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.lg)
            )
            .padding(Theme.spacing._8)
            .padding(end = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(Theme.radius.sm))
                    .background(Theme.colorScheme.background.surfaceHigh)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(Theme.radius.md))
                        .background(Theme.colorScheme.background.surfaceHigh)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(Theme.radius.md))
                        .background(Theme.colorScheme.background.surfaceHigh)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh)
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing._8))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
        )

        Spacer(modifier = Modifier.height(Theme.spacing._8))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh)
            )
            Box(
                modifier = Modifier
                    .size(height = 18.dp, width = 1.dp)
                    .background(color = Theme.colorScheme.stroke)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh)
            )
        }
    }
}

@Preview
@Composable
private fun AddressCardShimmerPreview() {
    MenaTheme {
        Column {
            repeat(3) {
                AddressCardShimmer(
                    modifier = Modifier.padding(bottom = Theme.spacing._12)
                )
            }
        }
    }
}