package net.thechance.mena.faith.presentation.feature.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.am_label
import mena.faith_presentation.generated.resources.asr
import mena.faith_presentation.generated.resources.dhuhr
import mena.faith_presentation.generated.resources.fajr
import mena.faith_presentation.generated.resources.ic_column_mosque
import mena.faith_presentation.generated.resources.ic_mosque_bg
import mena.faith_presentation.generated.resources.ic_triangle_down
import mena.faith_presentation.generated.resources.isha
import mena.faith_presentation.generated.resources.mosque_image_description
import mena.faith_presentation.generated.resources.pm_label
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.PrayerTimesUiState
import net.thechance.mena.faith.presentation.feature.main.PrayerUiModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PrayerTimesCard(
    prayerTimesUiState: PrayerTimesUiState?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (prayerTimesUiState == null) return

    Box(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(2.65f)
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .padding(top = Theme.spacing._16)
                .clip(RoundedCornerShape(Theme.radius.lg))
                .background(Theme.colorScheme.background.surfaceLow)
        )
        Image(
            painter = painterResource(Res.drawable.ic_mosque_bg),
            contentDescription = stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.35f)
                .aspectRatio(1.52f),
            contentScale = ContentScale.Fit,
        )
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding = PaddingValues(horizontal = Theme.spacing._12),
        ) {
            itemsIndexed(prayerTimesUiState.prayers) { index, prayer ->
                PrayerItem(
                    prayer = prayer,
                    isNextPrayer = index == prayerTimesUiState.nextPrayerIndex,
                )
            }
        }


        Image(
            painter = painterResource(Res.drawable.ic_column_mosque),
            contentDescription = stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .padding(start = 22.dp),
            contentScale = ContentScale.Crop,
        )
    }
}


@Composable
private fun PrayerItem(
    prayer: PrayerUiModel,
    isNextPrayer: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._2, vertical = Theme.spacing._8),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(prayer.displayName),
                color = Theme.colorScheme.secondary.secondaryText,
                style = Theme.typography.label.small,
            )
            Text(
                text = prayer.time,
                color = Theme.colorScheme.secondary.secondary,
                style = Theme.typography.title.medium,
            )
            Text(
                text = if (prayer.isAM) stringResource(Res.string.am_label)
                else stringResource(Res.string.pm_label),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.secondary.secondaryText,
            )
        }

        if (isNextPrayer) {
            Icon(
                painter = painterResource(Res.drawable.ic_triangle_down),
                contentDescription = null,
                tint = Theme.colorScheme.secondary.secondaryText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(Theme.spacing._16),
            )
        }
    }
}


@Preview
@Composable
private fun PrayerTimesCardPreview() {
    val samplePrayerTimesUiState = PrayerTimesUiState(
        prayers = listOf(
            PrayerUiModel(
                name = PrayerName.FAJR,
                displayName = Res.string.fajr,
                time = "06:00",
                isAM = true
            ),
            PrayerUiModel(
                name = PrayerName.DHUHR,
                displayName = Res.string.dhuhr,
                time = "12:00",
                isAM = false
            ),
            PrayerUiModel(
                name = PrayerName.ASR,
                displayName = Res.string.asr,
                time = "04:00",
                isAM = false
            ),
            PrayerUiModel(
                name = PrayerName.MAGHRIB,
                displayName = Res.string.fajr,
                time = "06:00",
                isAM = false
            ),
            PrayerUiModel(
                name = PrayerName.ISHA,
                displayName = Res.string.isha,
                time = "08:00",
                isAM = false
            )
        ),
        nextPrayerIndex = 0
    )
    QuranTheme {
        PrayerTimesCard(
            prayerTimesUiState = samplePrayerTimesUiState,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val samplePrayer = PrayerUiModel(
        name = PrayerName.FAJR,
        displayName = Res.string.fajr,
        time = "06:00",
        isAM = true
    )
    MenaTheme {
        QuranTheme {
            PrayerItem(prayer = samplePrayer, isNextPrayer = true)
        }
    }
}
