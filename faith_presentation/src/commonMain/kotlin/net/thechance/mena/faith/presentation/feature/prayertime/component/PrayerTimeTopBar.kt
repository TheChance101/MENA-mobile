package net.thechance.mena.faith.presentation.feature.prayertime.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.icon_location
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeUiState
import net.thechance.mena.faith.presentation.utils.extentions.noRippleClickable
import net.thechance.mena.faith.presentation.utils.extentions.takeCityAndCountry
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PrayerTimeTopBar(uiState: PrayerTimeUiState, onClick: () -> Unit) {

    Row(
        modifier = Modifier.background(
            shape = RoundedCornerShape(Theme.radius.full),
            color = Theme.colorScheme.background.surfaceLow
        )
            .height(Theme.spacing._24)
            .noRippleClickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(Res.drawable.ic_location),
            contentDescription = stringResource(Res.string.icon_location),
            modifier = Modifier
                .padding(start = Theme.spacing._4)
                .size(Theme.spacing._16)
        )

        Text(
            text = uiState.address.takeCityAndCountry(),
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.small,
            modifier = Modifier.padding(end = Theme.spacing._8),
            maxLines = 1
        )
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            PrayerTimeTopBar(
                uiState = PrayerTimeUiState(address = "Baghdad, Iraq"),
                onClick = {}
            )
        }
    }
}
