package net.thechance.mena.faith.presentation.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.asr
import mena.faith_presentation.generated.resources.dhuhr
import mena.faith_presentation.generated.resources.fajr
import mena.faith_presentation.generated.resources.ic_sunrise
import mena.faith_presentation.generated.resources.isha
import mena.faith_presentation.generated.resources.sunrise_time_label
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.components.FaithFeatureCard
import net.thechance.mena.faith.presentation.feature.main.components.MainTopBar
import net.thechance.mena.faith.presentation.feature.main.components.PrayerTimesCard
import net.thechance.mena.faith.presentation.feature.main.components.SunriseTimeRow
import net.thechance.mena.faith.presentation.feature.main.components.TilawahSection
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import net.thechance.mena.faith.presentation.navigation.Route.SurahDetailsRoute
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = LocalNavController.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshTilawah()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is MainScreenEffect.NavigateToSurah -> {
                navController.navigate(
                    SurahDetailsRoute(
                        surahId = effect.surahId,
                        ayahNumber = effect.ayahNumber
                    )
                )
            }

            MainScreenEffect.NavigateToQuran -> navController.navigate(Route.SurRoute)
            MainScreenEffect.NavigateToQiblah -> navController.navigate(Route.CalibrateDeviceRoute)
            MainScreenEffect.NavigateToMosques -> navController.navigate(Route.NearbyMosquesRoute)
            MainScreenEffect.NavigateToPrayerTime -> navController.navigate(Route.PrayerTimeRoute)
            MainScreenEffect.NavigateToTilawah -> navController.navigate(Route.DownloadedSurScreen())
            MainScreenEffect.NavigateToAddressesScreen -> navController.navigate(Route.UserAddresses)
        }
    }

    Content(
        uiState = uiState,
        listener = viewModel
    )
}

@Composable
private fun Content(
    uiState: MainUiState,
    listener: MainInteractionListener
) {
    Scaffold(
        topBar = {
            MainTopBar(
                locationName = uiState.address,
                onLocationChange = listener::onLocationClick
            )
        }
    ) {
        val faithFeatureCards = faithFeatureCards(listener = listener)

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(
                bottom = Theme.spacing._8,
                start = Theme.spacing._16,
                end = Theme.spacing._16
            ),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PrayerSection(uiState, listener)
            }

            items(faithFeatureCards) { card ->
                FaithFeatureCard(
                    icon = card.icon,
                    title = card.title,
                    onClick = card.onClick,
                )
            }
        }
    }
}

@Composable
private fun PrayerSection(
    uiState: MainUiState,
    listener: MainInteractionListener
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        PrayerTimesCard(
            prayerTimesUiState = uiState.prayerTimesUiState,
            onClick = listener::onPrayerTimeClick
        )
        if (uiState.hijriDate.isNotBlank()) {
            Text(
                text = uiState.hijriDate,
                color = Theme.colorScheme.shadeSecondary,
                style = Theme.typography.label.extraSmall
            )
        }

        if (uiState.sunriseTime.isNotBlank()) {
            SunriseTimeRow(
                icon = painterResource(Res.drawable.ic_sunrise),
                title = stringResource(Res.string.sunrise_time_label),
                time = uiState.sunriseTime,
                modifier = Modifier.padding(vertical = Theme.spacing._12)
            )
        }

        TilawahSection(
            tilawahUiState = uiState.tilawahUiState,
            onContinueTilawahClick = onContinueTilawahClick(uiState, listener),
            modifier = Modifier.padding(bottom = Theme.spacing._8)
        )
    }
}

private fun onContinueTilawahClick(
    uiState: MainUiState,
    listener: MainInteractionListener
): () -> Unit = {
    uiState.tilawahUiState?.let { tilawah ->
        listener.onContinueTilawahClick(
            surahId = tilawah.surahId,
            surahName = tilawah.surahName,
            ayahNumber = tilawah.ayahNumber
        )
    }
}

@Composable
@Preview
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                uiState = MainUiState(
                    prayerTimesUiState = PrayerTimesUiState(
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
                    ),
                    hijriDate = "15 Ramadan 1446",
                    sunriseTime = "06:00 AM",
                    tilawahUiState = TilawahUiState(
                        surahId = 1,
                        surahName = "Al-Fatihah",
                        ayahNumber = 3,
                    ),
                    address = "street 23 abo ahmed, Baghdad, Iraq"
                ),
                listener = object : MainInteractionListener {
                    override fun onQuranClick() {}
                    override fun onQiblahClick() {}
                    override fun onMosquesClick() {}
                    override fun onPrayerTimeClick() {}
                    override fun onTilawahClick() {}
                    override fun onLocationClick() {}
                    override fun onContinueTilawahClick(
                        surahId: Int,
                        surahName: String,
                        ayahNumber: Int
                    ) {
                    }
                }
            )
        }
    }
}
