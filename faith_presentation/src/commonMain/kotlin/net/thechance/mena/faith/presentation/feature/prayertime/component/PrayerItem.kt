package net.thechance.mena.faith.presentation.feature.prayertime.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.fajr
import mena.faith_presentation.generated.resources.ic_next_prayer_arrow
import mena.faith_presentation.generated.resources.next_prayer_time_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PrayerItem(
    prayerNameResource: StringResource,
    prayerTime: String,
    isNextPrayer: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(bottom = Theme.spacing._8)
            .padding(horizontal = Theme.spacing._16)
            .background(
                Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.sm)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isNextPrayer) {
                Icon(
                    painter = painterResource(Res.drawable.ic_next_prayer_arrow),
                    contentDescription = stringResource(Res.string.next_prayer_time_icon),
                    modifier = Modifier.padding(end = Theme.spacing._8)
                )
            }

            Text(
                text = stringResource(prayerNameResource),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeSecondary,
                modifier = Modifier.padding(
                    start = if (isNextPrayer) 0.dp else Theme.spacing._16,
                    top = Theme.spacing._12,
                    bottom = Theme.spacing._12
                )
            )
        }

        Text(
            prayerTime,
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(
                end = Theme.spacing._12,
                top = Theme.spacing._12,
                bottom = Theme.spacing._12
            )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            PrayerItem(
                prayerNameResource = Res.string.fajr,
                prayerTime = "1:20 AM",
                isNextPrayer = true,
            )
        }
    }
}
