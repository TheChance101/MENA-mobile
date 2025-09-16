package net.thechance.mena.faith.presentation.feature.quran.bookmark

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.component.BackIcon
import net.thechance.mena.faith.presentation.feature.quran.bookmark.component.AyaBookmarkCard
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = koinViewModel(),
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val effect by viewModel.uiEffect.collectAsState(initial = null)

    LaunchedEffect(effect) {
        effect?.let { currentEffect ->
            when (currentEffect) {
                is BookmarkEffect.NavigateBack -> {
                    // TODO(navigate back)
                }
            }
        }
    }

    Content(
        uiState = state,
        interactionListener = viewModel
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun Content(
    uiState: BookmarksScreenState,
    interactionListener: BookmarkInteractionListener
) {
    var swipedBookmarkId by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16).statusBarsPadding(),
        contentPadding = PaddingValues(bottom = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        item {
            AppBar(
                title = stringResource(Res.string.bookmark),
                contentPadding = PaddingValues(vertical = Theme.spacing._8),
                leadingContent = { BackIcon() },
                onLeadingClick = interactionListener::onBackClick
            )
        }
        items(
            items = uiState.bookmarks,
            key = { it.bookmarkId }
        ) { bookmark ->
            AyaBookmarkCard(
                id = bookmark.bookmarkId,
                surahName = bookmark.surahName,
                ayaNumber = bookmark.ayaNumber,
                timeAgo = bookmark.createdAt,
                ayaText = bookmark.ayaText,
                isSwiped = swipedBookmarkId == bookmark.bookmarkId,
                onSwipe = { swipedBookmarkId = it },
                onRemoveBookmarkClick = {
                    interactionListener.onRemoveBookmarkClick(bookmark.bookmarkId)
                },
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = tween(500),
                        fadeOutSpec = tween(500)
                    )
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun BookmarkScreenPreview() {
    MenaTheme {
        Content(
            uiState = BookmarksScreenState(
                bookmarks = listOf(
                    BookmarksScreenState.BookmarkUiState(
                        1,
                        "Al-Baqarah",
                        134,
                        "تِلْكَ أُمَّةٌ قَدْ خَلَتْ لَهَا مَا كَسَبَتْ وَلَكُمْ مَا كَسَبْتُمْ وَلَا تُسْأَلُونَ عَمَّا كَانُوا يَعْمَلُونَ",
                        Clock.System.now()
                    ),
                    BookmarksScreenState.BookmarkUiState(
                        2,
                        "Al-Nisa",
                        176,
                        "يَسْتَفْتُونَكَ قُلِ اللَّهُ يُفْتِيكُمْ فِي الْكَلَالَةِ ۚ إِنِ امْرُؤٌ هَلَكَ لَيْسَ لَهُ وَلَدٌ وَلَهُ أُخْتٌ فَلَهَا نِصْفُ مَا تَرَكَ ۚ وَهُوَ يَرِثُهَا إِن لَّمْ يَكُن لَّهَا وَلَدٌ ۚ فَإِن كَانَتَا اثْنَتَيْنِ فَلَهُمَا الثُّلُثَانِ مِمَّا تَرَكَ ۚ وَإِن كَانُوا إِخْوَةً رِّجَالًا وَنِسَاءً فَلِلذَّكَرِ مِثْلُ حَظِّ الْأُنْثَيَيْنِ ۗ يُبَيِّنُ اللَّهُ لَكُمْ أَنْ تَضِلُّوا ۗ وَاللَّهُ بِكُلِّ شَيْءٍ عَلِيمٌَ",
                        Clock.System.now()
                    ),
                    BookmarksScreenState.BookmarkUiState(
                        3,
                        "Al-A'raf",
                        172,
                        "وَإِذْ أَخَذَ رَبُّكَ مِنْ بَنِي آدَمَ مِنْ ظُهُورِهِمْ ذُرِّيَّتَهُمْ وَأَشْهَدَهُمْ عَلَى أَنْفُسِهِمْ أَلَسْتُ بِرَبِّكُمْ قَالُوا بَلَى شَهِدْنَا أَنْ تَقُولُوا يَوْمَ الْقِيَامَةِ إِنَّا كُنَّا عَنْ هَذَا غَافِلِينَ",
                        Clock.System.now()
                    ),
                    BookmarksScreenState.BookmarkUiState(
                        4,
                        "Al-Baqarah",
                        152,
                        "فَاذْكُرُونِي أَذْكُرْكُمَْ",
                        Clock.System.now()
                    ),
                    BookmarksScreenState.BookmarkUiState(
                        5,
                        "Al-Maidah",
                        3,
                        "حُرِّمَتْ عَلَيْكُمُ الْمَيْتَةُ وَالدَّمُ وَلَحْمُ الْخِنْزِيرِ وَمَا أُهِلَّ بِهِ لِغَيْرِ اللَّهِ وَالْمُنْخَنِقَةُ وَالْمَوْقُوذَةُ وَالْمُتَرَدِّيَةُ وَالنَّطِيحَةُ وَمَا أَكَلَ السَّبُعُ إِلَّا مَا ذَكَّيْتُمْ وَمَا ذُبِحَ عَلَى النُّصُبِ وَأَن تَسْتَقْسِمُوا بِالْأَزْلَامِ ۚ ذَٰلِكُمْ فِسْقٌ ۗ الْيَوْمَ يَئِسَ الَّذِينَ كَفَرُوا مِن دِينِكُمْ فَلَا تَخْشَوْهُمْ وَاخْشَوْنِ ۚ الْيَوْمَ أَكْمَلْتُ لَكُمْ دِينَكُمْ وَأَتْمَمْتُ عَلَيْكُمْ نِعْمَتِي وَرَضِيتُ لَكُمُ الْإِسْلَامَ دِينًا ۚ فَمَنِ اضْطُرَّ فِي مَخْمَصَةٍ غَيْرَ مُتَجَانِفٍ لِّإِثْمٍ فَإِنَّ اللَّهَ غَفُورٌ رَّحِيمٌَ",
                        Clock.System.now()
                    ),
                    BookmarksScreenState.BookmarkUiState(
                        6,
                        "Al-Noor",
                        354,
                        "اللَّهُ نُورُ السَّمَاوَاتِ وَالْأَرْضٌَِ",
                        Clock.System.now()
                    ),
                    BookmarksScreenState.BookmarkUiState(
                        7,
                        "Al-Baqarah",
                        134,
                        "تِلْكَ أُمَّةٌ قَدْ خَلَتْ لَهَا مَا كَسَبَتْ وَلَكُمْ مَا كَسَبْتُمْ وَلَا تُسْأَلُونَ عَمَّا كَانُوا يَعْمَلُونَ",
                        Clock.System.now()
                    ),
                ),
            ),
            interactionListener = object : BookmarkInteractionListener {
                override fun onBackClick() {}
                override fun onRemoveBookmarkClick(id: Int) {}
            }
        )
    }
}
