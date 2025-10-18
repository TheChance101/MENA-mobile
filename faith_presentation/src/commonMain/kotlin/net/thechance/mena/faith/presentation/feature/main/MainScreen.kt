package net.thechance.mena.faith.presentation.feature.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.faith_title
import mena.faith_presentation.generated.resources.ic_column_mosque
import mena.faith_presentation.generated.resources.ic_kaaba
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.ic_mosque
import mena.faith_presentation.generated.resources.ic_quran
import mena.faith_presentation.generated.resources.ic_sunrise
import mena.faith_presentation.generated.resources.location
import mena.faith_presentation.generated.resources.mosque_image_description
import mena.faith_presentation.generated.resources.nearby_mosques
import mena.faith_presentation.generated.resources.qiblah_direction
import mena.faith_presentation.generated.resources.quran_kareem
import mena.faith_presentation.generated.resources.sunrise_time_label
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.feature.main.componant.PrayerTimesCard
import net.thechance.mena.faith.presentation.feature.main.componant.SunriseTimeRow
import net.thechance.mena.faith.presentation.feature.main.componant.TilawahSection
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
                    Route.SurahDetailsRoute(
                        surahId = effect.surahId,
                        surahName = effect.surahName,
                        ayahNumber = effect.ayahNumber
                    )
                )
            }

            MainScreenEffect.NavigateToQuran -> navController.navigate(Route.SurRoute)
            MainScreenEffect.NavigateToQiblah -> navController.navigate(Route.CalibrateDeviceRoute)
            MainScreenEffect.NavigateToMosques -> {}
        }
    }

    Content(
        uiState = uiState,
        listener = viewModel
    )
}

@Composable
private fun Content(
    uiState: MainScreenState,
    listener: MainInteractionListener
) {
    Scaffold(
        topBar = { MainTopBar() }
    ) {
        val featureCards = listOf(
            FeatureItem(
                title = stringResource(Res.string.quran_kareem),
                icon = painterResource(Res.drawable.ic_quran),
                onClick = listener::onQuranClick
            ),
            FeatureItem(
                title = stringResource(Res.string.qiblah_direction),
                icon = painterResource(Res.drawable.ic_kaaba),
                onClick = listener::onQiblahClick
            ),
            FeatureItem(
                title = stringResource(Res.string.nearby_mosques),
                icon = painterResource(Res.drawable.ic_mosque),
                onClick = listener::onMosquesClick
            )
        )
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = Theme.spacing._8,
                    top = Theme.spacing._16,
                    start = Theme.spacing._16,
                    end = Theme.spacing._16
                ),
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PrayerTimesCard(prayerTimesUiState = uiState.prayerTimesUiState)
                    Text(
                        text = uiState.hijriDate,
                        color = Theme.colorScheme.shadeSecondary,
                        style = Theme.typography.label.extraSmall
                    )
                    SunriseTimeRow(
                        icon = painterResource(Res.drawable.ic_sunrise),
                        title = stringResource(Res.string.sunrise_time_label),
                        time = uiState.sunriseTime
                    )
                    TilawahSection(
                        tilawahUiState = uiState.tilawahUiState,
                        onContinueTilawahClick = {
                            uiState.tilawahUiState?.let { tilawah ->
                                listener.onContinueTilawahClick(
                                    surahId = tilawah.surahId,
                                    surahName = tilawah.surahName,
                                    ayahNumber = tilawah.ayahNumber
                                )
                            }
                        },
                        modifier = Modifier.padding(bottom = Theme.spacing._8)
                    )
                }
            }

            items(featureCards) { card ->
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
private fun MainTopBar() {
    AppBar(
        title = stringResource(Res.string.faith_title),
        trailingContent = {
            Row(
                modifier = Modifier
                    .background(
                        color = Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(Theme.radius.full)
                    )
                    .padding(horizontal = Theme.spacing._8, vertical = Theme.spacing._4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_location),
                    contentDescription = stringResource(Res.string.location),
                    tint = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Cairo, Egypt",
                    color = Theme.colorScheme.shadePrimary,
                    style = Theme.typography.label.small
                )
            }
        }
    )
}

@Composable
private fun FaithFeatureCard(
    icon: Painter,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,

        ) {
        Image(
            painter = painterResource(Res.drawable.ic_column_mosque),
            stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(100.dp),
            contentScale = ContentScale.Fit,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = Theme.spacing._12),
            verticalArrangement = Arrangement.spacedBy(8.dp),

            ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Start)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = Theme.colorScheme.primary.primary,
                    modifier = Modifier.size(28.dp)
                        .padding(bottom = Theme.spacing._4)
                )
            }
            Text(
                text = title,
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
            )
        }
    }
}

data class FeatureItem(
    val title: String,
    val icon: Painter,
    val onClick: () -> Unit
)
