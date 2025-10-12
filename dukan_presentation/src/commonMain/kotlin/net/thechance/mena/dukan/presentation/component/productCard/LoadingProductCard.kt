package net.thechance.mena.dukan.presentation.component.productCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadingProductCard(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(size = Theme.radius.md)
            ).height(104.dp)
            .padding(Theme.spacing._4),
    ) {
        Box(
            modifier = Modifier.background(
                color = Theme.colorScheme.background.surfaceHigh,
                shape = RoundedCornerShape(
                    topStart = Theme.radius.md,
                    bottomStart = Theme.radius.md
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(Theme.radius.sm))
                    .background(Theme.colorScheme.background.surfaceHigh),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = Theme.spacing._8,
                    top = Theme.spacing._4,
                    end = Theme.spacing._4
                ),
        ) {
            Box(
                modifier = Modifier
                    .height(Theme.spacing._16)
                    .fillMaxWidth(0.33f)
                    .clip(RoundedCornerShape(Theme.radius.full))
                    .background(Theme.colorScheme.background.surfaceHigh)
            )
            Box(
                modifier = Modifier
                    .padding(top = Theme.spacing._2)
                    .height(Theme.spacing._32)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh)
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
                    Box(
                        modifier = Modifier
                            .height(Theme.spacing._24)
                            .fillMaxWidth(0.25f)
                            .clip(RoundedCornerShape(Theme.radius.full))
                            .background(Theme.colorScheme.background.surfaceHigh)
                    )

                    Image(
                        painter = painterResource(Res.drawable.silver_tc),
                        contentDescription = stringResource(Res.string.koin_icon),
                        modifier = Modifier
                            .padding(start = Theme.spacing._4)
                            .size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}