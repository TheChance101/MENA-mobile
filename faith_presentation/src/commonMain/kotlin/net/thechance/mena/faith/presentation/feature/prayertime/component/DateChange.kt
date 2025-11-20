package net.thechance.mena.faith.presentation.feature.prayertime.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.arrow_right
import mena.faith_presentation.generated.resources.back_icon
import mena.faith_presentation.generated.resources.dropdown_icon
import mena.faith_presentation.generated.resources.ic_arrow_down
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeUiState
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.convertIslamicDateToString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
internal fun DateChange(
    uiState: PrayerTimeUiState,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onDropDownClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Theme.spacing._24)
            .background(color = Theme.colorScheme.background.surfaceLow)
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(Res.drawable.arrow_left),
            contentDescription = stringResource(Res.string.back_icon),
            modifier = Modifier
                .size(Theme.spacing._16)
                .clickable(onClick = onPrevClick)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.currentDate.convertIslamicDateToString(),
                style = Theme.typography.label.extraSmall,
                color = Theme.colorScheme.shadeSecondary
            )
            Image(
                painterResource(Res.drawable.ic_arrow_down),
                contentDescription = stringResource(Res.string.dropdown_icon),
                modifier = Modifier
                    .size(Theme.spacing._16)
                    .clickable(onClick = onDropDownClick)
            )
        }
        Icon(
            painterResource(Res.drawable.arrow_right),
            contentDescription = stringResource(Res.string.arrow_right),
            modifier = Modifier
                .size(Theme.spacing._16)
                .clickable(onClick = onNextClick)
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            DateChange(
                uiState = PrayerTimeUiState(),
                onPrevClick = {},
                onNextClick = {},
                onDropDownClick = {}
            )
        }
    }
}
