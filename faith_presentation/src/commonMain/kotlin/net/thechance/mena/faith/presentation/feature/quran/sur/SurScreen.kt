package net.thechance.mena.faith.presentation.feature.quran.sur

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ayat
import mena.faith_presentation.generated.resources.ayat_count_format
import mena.faith_presentation.generated.resources.back_icon
import mena.faith_presentation.generated.resources.bookmark_icon
import mena.faith_presentation.generated.resources.ic_al_fatihah
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_bookmark
import mena.faith_presentation.generated.resources.ic_moshaf
import mena.faith_presentation.generated.resources.ic_surah_number_container
import mena.faith_presentation.generated.resources.madani
import mena.faith_presentation.generated.resources.makki
import mena.faith_presentation.generated.resources.moshaf_icon
import mena.faith_presentation.generated.resources.quran
import mena.faith_presentation.generated.resources.sur
import mena.faith_presentation.generated.resources.surah_arabic_name_icon
import mena.faith_presentation.generated.resources.surah_number_container_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SurScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSurahDetails: (surahId: Int, surahName: String) -> Unit,
    viewModel: SurViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val effect by viewModel.uiEffect.collectAsState(initial = null)

    LaunchedEffect(effect) {
        effect?.let { currentEffect ->
            when (currentEffect) {
                is SurEffect.NavigateToBack -> {
                    onNavigateBack()
                }

                is SurEffect.NavigateToBookmark -> {
                    onNavigateToBookmarks()
                }

                is SurEffect.NavigateToSurahDetails -> {
                    onNavigateToSurahDetails(currentEffect.surahId, currentEffect.surahName)
                }
            }
        }
    }

    Content(
        uiState = state,
        interactionListener = viewModel
    )
}

@Composable
private fun Content(
    uiState: SurScreenState,
    interactionListener: SurInteractionListener,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16).statusBarsPadding(),
        contentPadding = PaddingValues(bottom = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
    ) {
        item {
            Topbar(
                onBackClick = interactionListener::onBackClick,
                onBookmarkClick = interactionListener::onBookmarkClick,
            )
        }

        item {
            MenaText(
                text = stringResource(resource = Res.string.sur),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(bottom = Theme.spacing._4)
            )
        }

        items(uiState.sur) { surah ->
            SurahItem(
                surah = surah,
                onClick = { interactionListener.onSurahClick(surah.id, surah.surahName) }
            )
        }
    }
}

@Composable
private fun Topbar(
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppBar(
        title = stringResource(resource = Res.string.quran),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Theme.spacing._8),
        leadingContent = {
            MenaIcon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(resource = Res.string.back_icon),
                tint = Theme.colorScheme.primary.primary,
                modifier = Modifier.size(20.dp)
            )
        },
        onLeadingClick = onBackClick,
        trailingContent = { AppBarBookmarkOption(onBookmarkClick) }
    )
}

@Composable
private fun AppBarBookmarkOption(
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppBarOptionContainer(
        onClick = onBookmarkClick,
        content = {
            MenaIcon(
                painter = painterResource(Res.drawable.ic_bookmark),
                contentDescription = stringResource(resource = Res.string.bookmark_icon),
                tint = Theme.colorScheme.primary.primary,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun SurahItem(
    surah: SurScreenState.SurahUiState,
    onClick: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clickable { onClick(surah.id) }
            .padding(vertical = Theme.spacing._8, horizontal = Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SurahNumberContainer(
            surahNumber = surah.surahOrder,
            modifier = Modifier.padding(end = Theme.spacing._12)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            MenaText(
                text = surah.surahName,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(bottom = Theme.spacing._2)
            )

            SurahDetailsRow(ayatNumber = surah.ayatCount, isMakki = surah.isMakki)
        }

        MenaIcon(
            painter = painterResource(resource = surah.arabicNameImg),
            contentDescription = stringResource(resource = Res.string.surah_arabic_name_icon),
            tint = Theme.colorScheme.shadePrimary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun SurahDetailsRow(
    ayatNumber: Int,
    isMakki: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenaIcon(
            painter = painterResource(resource = Res.drawable.ic_moshaf),
            tint = Theme.colorScheme.shadeSecondary,
            contentDescription = stringResource(resource = Res.string.moshaf_icon),
            modifier = Modifier
                .size(16.dp)
                .padding(end = Theme.spacing._4)
        )

        MenaText(
            text = stringResource(
                resource = Res.string.ayat_count_format,
                ayatNumber,
                stringResource(resource = Res.string.ayat)
            ),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )

        Box(
            modifier = Modifier
                .padding(horizontal = Theme.spacing._8)
                .size(3.dp)
                .background(
                    color = Theme.colorScheme.shadeTertiary,
                    shape = RoundedCornerShape(Theme.radius.full)
                )
        )

        MenaText(
            text = if (isMakki) stringResource(resource = Res.string.makki)
            else stringResource(resource = Res.string.madani),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Composable
private fun SurahNumberContainer(
    surahNumber: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        MenaIcon(
            painter = painterResource(resource = Res.drawable.ic_surah_number_container),
            contentDescription = stringResource(resource = Res.string.surah_number_container_icon),
            modifier = Modifier.fillMaxSize()
        )

        MenaText(
            text = surahNumber.twoDigitsMinimum(),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.secondary.secondary
        )
    }
}

private fun Int.twoDigitsMinimum(): String = this.toString().padStart(2, '0')

@Preview
@Composable
private fun SurScreenPreview() {
    MenaTheme {
        Content(
            uiState = SurScreenState(
                sur = listOf(
                    SurScreenState.SurahUiState(
                        id = 1,
                        surahOrder = 1,
                        surahName = "Al Fatihah",
                        arabicNameImg = Res.drawable.ic_al_fatihah,
                        ayatCount = 7,
                        isMakki = true
                    ),
                    SurScreenState.SurahUiState(
                        id = 1,
                        surahOrder = 1,
                        surahName = "Al Fatihah",
                        arabicNameImg = Res.drawable.ic_al_fatihah,
                        ayatCount = 7,
                        isMakki = true
                    ),
                    SurScreenState.SurahUiState(
                        id = 1,
                        surahOrder = 1,
                        surahName = "Al Fatihah",
                        arabicNameImg = Res.drawable.ic_al_fatihah,
                        ayatCount = 7,
                        isMakki = true
                    )
                )
            ),
            interactionListener = object : SurInteractionListener {
                override fun onSurahClick(surahId: Int, surahName: String) = Unit
                override fun onBackClick() = Unit
                override fun onBookmarkClick() = Unit
            }
        )
    }
}
