package net.thechance.mena.faith.presentation.feature.prayertime

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.prayer_time
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.getPrayerDisplayNameResource
import net.thechance.mena.faith.presentation.feature.prayertime.component.DateChange
import net.thechance.mena.faith.presentation.feature.prayertime.component.IslamicDatePickerDialog
import net.thechance.mena.faith.presentation.feature.prayertime.component.NextPrayerCard
import net.thechance.mena.faith.presentation.feature.prayertime.component.PrayerItem
import net.thechance.mena.faith.presentation.feature.prayertime.component.PrayerTimeTopBar
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.formatInstantToTimeString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun PrayerTimeScreen(
    viewModel: PrayerTimeViewModel = koinViewModel(),
) {

    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            PrayerTimeEffect.NavigateBack -> navController.navigateUp()
            PrayerTimeEffect.NavigateToAddressesScreen -> {
                navController.navigate(Route.UserAddresses)
            }
        }
    }
    Content(
        uiState = uiState,
        snackBarState = snackBarState,
        listener = viewModel
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun Content(
    uiState: PrayerTimeUiState,
    snackBarState: SnackBarState,
    listener: PrayerTimeInteractionListener
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.prayer_time),
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
                trailingContent = { PrayerTimeTopBar(uiState, listener::onLocationClick) }
            )
        }, overlays = {
            dialog(uiState.isDatePickerShown) {
                IslamicDatePickerDialog(
                    isVisible = uiState.isDatePickerShown,
                    islamicDatePickerUiState = uiState.islamicDatePickerUiState,
                    onDateChange = listener::onSelectedDateChange,
                    onConfirmDateClick = listener::onDateSelected,
                    onClearDateClick = listener::onClearSelectedDate,
                    onDismiss = listener::onDatePickerDismiss
                )
            }
        },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                isVisible = snackBarState.isVisible,
                status = snackBarState.status
            )
        }
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
                    prayerTime = prayer.time.formatInstantToTimeString(withISPM = true),
                    isNextPrayer = (prayer.name == uiState.nextPrayerName)
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                uiState = PrayerTimeUiState(),
                snackBarState = SnackBarState(),
                listener = object : PrayerTimeInteractionListener {
                    override fun onBackClick() {}
                    override fun onLocationClick() {}
                    override fun onSelectedDateChange(day: Int, month: Int, year: Int) {}
                    override fun onDateSelected() {}
                    override fun onClearSelectedDate() {}
                    override fun onDatePickerDismiss() {}
                    override fun onPrevDateClick() {}
                    override fun onNextDateClick() {}
                    override fun onDateDropdownClick() {}
                }
            )
        }
    }
}
