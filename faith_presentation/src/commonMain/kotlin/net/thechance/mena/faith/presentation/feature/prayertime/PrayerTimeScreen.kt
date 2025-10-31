package net.thechance.mena.faith.presentation.feature.prayertime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.arrow_right
import mena.faith_presentation.generated.resources.ic_arrow_down
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.ic_next_prayer_arrow
import mena.faith_presentation.generated.resources.ic_prayer_man
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.feature.main.getPrayerDisplayNameResource
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.formatInstantToTimeString
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun PrayerTimeScreen(
    viewModel: PrayerTimeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            PrayerTimeEffect.NavigateBack -> navController.navigateUp()
            PrayerTimeEffect.NavigateCalenderBottomSheet -> {}
            PrayerTimeEffect.NavigateNextDate -> {}
            PrayerTimeEffect.NavigatePrevDate -> {}
        }
    }
    Content(
        uiState = uiState,
        listener = viewModel
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun Content(uiState: PrayerTimeUiState, listener: PrayerTimeInteractionListener) {
    Scaffold(
        topBar = {
            AppBar(
                title = "Prayer time",
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16, vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.arrow_left)
                    )
                },
                onLeadingClick = listener::onBackClick,
                trailingContent = { PrayerTimeTopBar() }
            )
        },
    ) {
        LazyColumn {
            item {
                DateChange(
                    uiState = uiState,
                    onPrevClick = listener::onPrevDateClick,
                    onNextClick = listener::onNextDateClick,
                    onDropDownClick = listener::onDateDropdownClick
                )
            }
            item { NextPrayerCard(uiState = uiState) }

            items(items = uiState.prayerTimes) { prayer ->
                PrayerItem(
                    prayerNameResource = getPrayerDisplayNameResource(prayer.name),
                    prayerTime = formatInstantToTimeString(prayer.time),
                    isNextPrayer = prayer.name == uiState.nextPrayerName
                )
            }
        }
    }
}

@Composable
private fun PrayerTimeTopBar() {
    Row(
        modifier = Modifier.background(
            shape = RoundedCornerShape(Theme.radius.full),
            color = Theme.colorScheme.background.surfaceLow
        ).height(Theme.spacing._24),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_location),
            contentDescription = "icon_location",
            modifier = Modifier
                .padding(start = Theme.spacing._4)
                .size(16.dp)
        )

        Text(
            text = "Cairo, Egypt",
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.small,
            modifier = Modifier.padding(end = Theme.spacing._8)
        )
    }
}

@Composable
private fun DateChange(
    uiState: PrayerTimeUiState,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onDropDownClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Theme.spacing._24)
            .background(color = Theme.colorScheme.background.surfaceLow)
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(Res.drawable.arrow_left),
            contentDescription = "Back icon",
            modifier = Modifier
                .size(Theme.spacing._16)
                .clickable(onClick = onPrevClick)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.currentDate,
                style = Theme.typography.label.extraSmall,
                color = Theme.colorScheme.shadeSecondary
            )
            Image(
                painterResource(Res.drawable.ic_arrow_down),
                contentDescription = "Dropdown icon",
                modifier = Modifier
                    .size(Theme.spacing._16)
                    .clickable(onClick = onDropDownClick)
            )
        }
        Image(
            painterResource(Res.drawable.arrow_right),
            contentDescription = "Forward icon",
            modifier = Modifier
                .size(Theme.spacing._16)
                .clickable(onClick = onNextClick)
        )
    }
}

@Composable
private fun NextPrayerCard(uiState: PrayerTimeUiState) {
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
                .size(40.dp)
                .padding(start = Theme.spacing._12)
                .padding(vertical = Theme.spacing._8)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = Theme.spacing._8),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Next prayer ${stringResource(getPrayerDisplayNameResource(uiState.nextPrayerName))} in:",
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

@Composable
private fun PrayerItem(
    prayerNameResource: StringResource,
    prayerTime: String,
    isNextPrayer: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(bottom = Theme.spacing._12)
            .padding(horizontal = Theme.spacing._16)
            .padding(bottom = Theme.spacing._12)
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
                    contentDescription = "next prayer time icon",
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