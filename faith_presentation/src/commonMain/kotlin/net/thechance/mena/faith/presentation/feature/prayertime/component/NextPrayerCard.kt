package net.thechance.mena.faith.presentation.feature.prayertime.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_prayer_man
import mena.faith_presentation.generated.resources.next_prayer_in
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.getPrayerDisplayNameResource
import net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
internal fun NextPrayerCard(uiState: PrayerTimeUiState) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16)
            .padding(top = Theme.spacing._24, bottom = Theme.spacing._16)
            .background(
                Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.sm)
            ),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_prayer_man),
            contentDescription = "prayer man icon",
            modifier = Modifier
                .padding(start = Theme.spacing._12)
                .padding(vertical = Theme.spacing._8)
                .size(40.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = Theme.spacing._8),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(
                    Res.string.next_prayer_in,
                    stringResource(
                        getPrayerDisplayNameResource(
                            uiState.nextPrayerName ?: PrayerName.FAJR
                        )
                    )
                ),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary
            )
            Text(
                text = uiState.nextPrayerCountdown,
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.secondary.secondary
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            NextPrayerCard(
                uiState = PrayerTimeUiState()
            )
        }
    }
}
