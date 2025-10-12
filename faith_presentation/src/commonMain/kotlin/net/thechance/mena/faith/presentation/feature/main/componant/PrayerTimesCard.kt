package net.thechance.mena.faith.presentation.feature.main.componant

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.am_label
import mena.faith_presentation.generated.resources.ic_column_mosque
import mena.faith_presentation.generated.resources.ic_mosque_bg
import mena.faith_presentation.generated.resources.ic_triangle_down
import mena.faith_presentation.generated.resources.mosque_image_description
import mena.faith_presentation.generated.resources.pm_label
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.feature.main.PrayerTimesUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrayerTimesCard(
    prayerTimesUiState: PrayerTimesUiState?,
) {
    if (prayerTimesUiState == null) return

    var rowWidth by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier.aspectRatio(2.65f)
            .clip(RoundedCornerShape(Theme.radius.lg))
            .background(Theme.colorScheme.background.surfaceLow)
    ) {

        Image(
            painter = painterResource(Res.drawable.ic_mosque_bg),
            contentDescription = stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.35f)
                .aspectRatio(1f)
                .offset(y = (-38).dp, x = (-8).dp),
            contentScale = ContentScale.Fit,
        )

        Image(
            painter = painterResource(Res.drawable.ic_column_mosque),
            contentDescription = stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .offset(x = (16).dp),
            contentScale = ContentScale.Fit,
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    rowWidth = coordinates.size.width.toFloat()
                }
                .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._12),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            prayerTimesUiState.prayers.forEach { prayer ->
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(prayer.displayName),
                        color = Theme.colorScheme.secondary.secondaryText,
                        style = Theme.typography.label.small
                    )
                    Text(
                        text = prayer.time,
                        color = Theme.colorScheme.secondary.secondary,
                        style = Theme.typography.title.medium
                    )
                    Text(
                        text = if (prayer.isAM) stringResource(Res.string.am_label)
                        else stringResource(Res.string.pm_label),
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.shadeSecondary
                    )
                }
            }
        }

        val offsetAdjustment = when {
            rowWidth > 1500f -> 38.dp
            else -> 8.dp
        }

        val itemWidth = if (prayerTimesUiState.prayers.isNotEmpty()) rowWidth / prayerTimesUiState.prayers.size else 0f

        val indicatorOffsetPx by animateDpAsState(
            targetValue = with(LocalDensity.current) {
                (itemWidth * prayerTimesUiState.currentPrayerIndex + itemWidth / 2).toDp() + offsetAdjustment
            },
            label = "prayer_indicator_animation"
        )

        Icon(
            painter = painterResource(Res.drawable.ic_triangle_down),
            contentDescription = null,
            tint = Theme.colorScheme.secondary.secondaryText,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = indicatorOffsetPx, y = 4.dp)
                .size(16.dp)
        )
    }
}