package net.thechance.mena.core_chat.presentation.screen.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.am
import mena.core_chat_presentation.generated.resources.ic_prayer
import mena.core_chat_presentation.generated.resources.ic_tempreature
import mena.core_chat_presentation.generated.resources.next_prayer_in
import mena.core_chat_presentation.generated.resources.pm
import mena.core_chat_presentation.generated.resources.prayer_weather_pattern_shape
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState
import net.thechance.mena.core_chat.presentation.utils.formatAsTime
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeatherAndNextPrayerCard(
    isLoading: Boolean = false,
    prayerUiState: HomeScreenState.PrayerUiState? = null,
    weatherUiState: HomeScreenState.WeatherUiState? = null,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = isLoading,
        modifier = modifier
    ) {
        if (isLoading) {
            WeatherAndNextPrayerCardSkeleton()
        } else {
            WeatherAndPrayerContent(
                prayerUiState = prayerUiState,
                weatherUiState = weatherUiState,
            )
        }
    }
}

@Composable
private fun WeatherAndPrayerContent(
    prayerUiState: HomeScreenState.PrayerUiState?,
    weatherUiState: HomeScreenState.WeatherUiState?,
) {
    if (prayerUiState == null && weatherUiState == null) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.lg))
            .background(Theme.colorScheme.primary.primary)
    ) {
        Image(
            painter = painterResource(Res.drawable.prayer_weather_pattern_shape),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
            colorFilter = ColorFilter.tint(Theme.colorScheme.primary.onPrimary)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(
                Theme.spacing._12,
                Alignment.CenterVertically
            ),
            modifier = Modifier.padding(Theme.spacing._12).align(Alignment.Center)
        ) {
            if (weatherUiState != null) {
                val weatherCondition = weatherUiState.weatherCondition?.let {
                    stringResource(it)
                } ?: ""
                RowInfoCard(
                    leadingIcon = painterResource(Res.drawable.ic_tempreature),
                    leadingText = "${weatherUiState.currentTemperature}°C, $weatherCondition",
                    trailingText = "${weatherUiState.maxTemperature}°C - ${weatherUiState.minTemperature}°C",
                )
            }
            if (prayerUiState != null) {
                RowInfoCard(
                    leadingIcon = painterResource(Res.drawable.ic_prayer),
                    leadingText = stringResource(
                        Res.string.next_prayer_in,
                        stringResource(prayerUiState.displayName)
                    ),
                    trailingText = prayerUiState.time.formatAsTime(
                        stringResource(Res.string.am),
                        stringResource(Res.string.pm)
                    ),
                )
            }
        }
    }
}

@Composable
private fun RowInfoCard(
    leadingIcon: Painter,
    leadingText: String,
    trailingText: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(Theme.radius.sm))
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = leadingIcon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(Theme.colorScheme.primary.onPrimary)
            )
        }
        Text(
            text = leadingText,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.primary.onPrimaryBody,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = trailingText,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.primary.onPrimary,
        )
    }
}
