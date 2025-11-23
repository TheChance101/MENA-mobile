package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AnimatedAyahActionButtons
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AnimatedQuranPlayer
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AyatOfSurah
import net.thechance.mena.faith.presentation.feature.quran.surah.component.SurahAppBar
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import net.thechance.mena.faith.presentation.navigation.Route.SearchRoute
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SurahScreen(
    viewModel: SurahViewModel = koinViewModel(),
    onClickBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is SurahScreenEffect.NavigateBack -> onClickBack()
            is SurahScreenEffect.ShareAyah -> {
                navController.navigate(
                    route = Route.ShareAyahToChatRoute(
                        surahId = effect.surahId,
                        ayahNumber = effect.ayahNumber,
                        ayahContent = effect.ayahContent,
                    ),
                )
            }

            is SurahScreenEffect.NavigateToSearchScreen -> {
                navController.navigate(
                    SearchRoute(effect.surahId)
                )
            }

            is SurahScreenEffect.NavigateToDownloadedRecitersScreen -> {
                navController.navigate(Route.SurahRecitersRoute(effect.surahId))
            }

        }
    }

    Content(
        state = uiState,
        listener = viewModel,
        snackBarState = snackBarState
    )
}

@Composable
private fun Content(
    state: SurahUiState,
    listener: SurahInteractionListener,
    snackBarState: SnackBarState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SurahAppBar(
                surahName = state.surahName,
                onBackClick = listener::onBackClick,
                onSearchClick = listener::onSearchClick
            )
        },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                status = snackBarState.status,
                isVisible = snackBarState.isVisible,
                modifier = modifier.fillMaxWidth()
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {
            AyatOfSurah(
                listener = listener,
                state = state
            )

            AnimatedQuranPlayer(
                state = state,
                listener = listener,
                surahId = state.surahId,
                modifier = Modifier
                    .padding(
                        bottom = Theme.spacing._24,
                        start = Theme.spacing._16,
                        end = Theme.spacing._16
                    ).align(Alignment.BottomCenter)
            )

            AnimatedAyahActionButtons(
                state = state,
                listener = listener,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(Theme.spacing._16)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            CompositionLocalProvider(LocalNavController provides rememberNavController()) {
                Content(
                    state = SurahUiState(
                        surahId = 1,
                        surahName = "Al-Fatiha",
                        ayatOfSurah = listOf(
                            Ayah(
                                number = 1,
                                surahId = 1,
                                content = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                plainContent = "بسم الله الرحمن الرحيم"
                            ),
                            Ayah(
                                number = 2,
                                surahId = 1,
                                content = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                                plainContent = "الحمد لله رب العالمين"
                            ),
                            Ayah(
                                number = 3,
                                surahId = 1,
                                content = "الرَّحْمَٰنِ الرَّحِيمِ",
                                plainContent = "الرحمن الرحيم"
                            ),
                            Ayah(
                                number = 4,
                                surahId = 1,
                                content = "مَالِكِ يَوْمِ الدِّينِ",
                                plainContent = "مالك يوم الدين"
                            ),
                            Ayah(
                                number = 5,
                                surahId = 1,
                                content = "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                                plainContent = "إياك نعبد وإياك نستعين"
                            ),
                            Ayah(
                                number = 6,
                                surahId = 1,
                                content = "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                                plainContent = "اهدنا الصراط المستقيم"
                            ),
                            Ayah(
                                number = 7,
                                surahId = 1,
                                content = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                                plainContent = "صراط الذين أنعمت عليهم غير المغضوب عليهم ولا الضالين"
                            )
                        ),
                        isBasmalaVisible = true,
                        selectedAyahNumber = null,
                        isAyahActionButtonsVisible = false,
                        initialAyahToScroll = null
                    ),
                    listener = object : SurahInteractionListener {
                        override fun onBackClick() {}
                        override fun onDismissActionButtons() {}
                        override fun onShareClick(content: String) {}
                        override fun onBookmarkClick(ayahNumber: Int) {}
                        override fun onAyahLongPress(ayahContent: String, ayahIndex: Int) {}
                        override fun onSearchClick() {}
                        override fun onListenClick() {}
                        override fun onReciterClick(surahId: Int) {}
                        override fun onNextAyahClick() {}
                        override fun onPlayPauseClick() {}
                        override fun onRepeatAyahClick() {}
                        override fun onClosePlayerClick() {}
                        override fun onPreviousAyahClick() {}
                        override fun onCopyClick(ayahContent: String) {}
                        override fun onInitialAyahScrolled() {}
                        override fun highlightAyah(ayahNumber: Int) {}
                        override fun updateContinueTilawah(ayahNumber: Int) {}
                        override fun onConfigrationChange() {}
                        override fun playSurah(surahNumber: Int) {}
                    },
                    snackBarState = SnackBarState()
                )
            }
        }
    }
}