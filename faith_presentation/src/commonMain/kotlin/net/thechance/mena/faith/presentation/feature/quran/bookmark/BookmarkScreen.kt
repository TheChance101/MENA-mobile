package net.thechance.mena.faith.presentation.feature.quran.bookmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.bookmarks
import mena.faith_presentation.generated.resources.empty_state_bookmark_description
import mena.faith_presentation.generated.resources.empty_state_bookmark_image
import mena.faith_presentation.generated.resources.empty_state_bookmark_title
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_not_saved_book_mark
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.components.SwappableCard
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.bookmark.component.AyaBookmarkCard
import net.thechance.mena.faith.presentation.feature.quran.bookmark.component.EmptyBookmarkState
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.utils.extentions.paging.isEmpty
import net.thechance.mena.faith.presentation.utils.extentions.paging.isNotEmpty
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = koinViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is BookmarkEffect.NavigateBack -> navController.navigateUp()
        }
    }

    Content(
        uiState = state,
        listener = viewModel,
        snackBarState = snackBarState
    )
}

@Composable
private fun Content(
    uiState: BookMarkUiState,
    snackBarState: SnackBarState,
    listener: BookmarkInteractionListener
) {
    val bookmarks = uiState.bookmarks.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.bookmarks),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16, vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.arrow_left)
                    )
                },
                onLeadingClick = listener::onBackClick
            )
        },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                isVisible = snackBarState.isVisible,
                status = snackBarState.status
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Theme.colorScheme.background.surface)
                .padding(horizontal = Theme.spacing._16)
        ) {
            AnimatedVisibility(
                visible = bookmarks.isEmpty() && uiState.isLoading.not(),
                enter = fadeIn(tween()),
                exit = fadeOut(tween())
            ) {
                EmptyBookmarkState()
            }

            AnimatedVisibility(
                visible = bookmarks.isNotEmpty(),
                enter = fadeIn(tween()),
                exit = fadeOut(tween())
            ) {
                BookmarkItems(
                    bookmarks = bookmarks,
                    onRemoveBookmarkClick = listener::onDeleteBookmarkClick,
                )
            }
        }
    }

}

@Composable
private fun EmptyBookmarkState() {
    EmptyBookmarkState(
        title = stringResource(Res.string.empty_state_bookmark_title),
        icon = painterResource(Res.drawable.ic_not_saved_book_mark),
        contentDescription = stringResource(Res.string.empty_state_bookmark_image),
        subTitle = stringResource(Res.string.empty_state_bookmark_description),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = Theme.spacing._16),
    )
}

@Composable
private fun BookmarkItems(
    bookmarks: LazyPagingItems<BookMarkUiState.BookmarkCardUiState>,
    onRemoveBookmarkClick: (Int) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
    ) {
        items(
            count = bookmarks.itemCount,
            key = bookmarks.itemKey { bookmark -> bookmark.bookmarkId }
        ) { index ->
            bookmarks[index]?.let {
                SwappableCard(
                    id = it.bookmarkId,
                    onClick = { onRemoveBookmarkClick(it.bookmarkId) },
                    cardContent = { contentModifier ->
                        AyaBookmarkCard(
                            surahName = it.surahName,
                            ayaNumber = it.ayaNumber,
                            createdAt = it.createdAt,
                            ayaText = it.ayaText,
                            modifier = contentModifier
                        )
                    },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(500),
                        fadeOutSpec = tween(500)
                    )
                )
            }
        }
    }
}


@Composable
@Preview
private fun BookmarkScreenPreview() {
    QuranTheme {
        Content(
            uiState = BookMarkUiState(
                bookmarks = flowOf(
                    PagingData.from(
                        listOf(
                            BookMarkUiState.BookmarkCardUiState(
                                bookmarkId = 1,
                                surahName = "Al-Fatihah",
                                ayaNumber = 3,
                                ayaText = "الرَّحْمَٰنِ الرَّحِيمِ",
                                createdAt = TimeAgo(amount = 2, unit = TimeUnit.HOURS)
                            ),
                            BookMarkUiState.BookmarkCardUiState(
                                bookmarkId = 2,
                                surahName = "Al-Baqarah",
                                ayaNumber = 255,
                                ayaText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
                                createdAt = TimeAgo(amount = 1, unit = TimeUnit.DAYS)
                            ),
                            BookMarkUiState.BookmarkCardUiState(
                                bookmarkId = 3,
                                surahName = "Al-Ikhlas",
                                ayaNumber = 1,
                                ayaText = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                                createdAt = TimeAgo(amount = 3, unit = TimeUnit.DAYS)
                            )
                        )
                    )
                ),
                isLoading = false,
                error = null
            ),
            listener = object : BookmarkInteractionListener {
                override fun onBackClick() {}
                override fun onDeleteBookmarkClick(id: Int) {}
                override fun onStartTilawahClick() {}
            },
            snackBarState = SnackBarState()
        )
    }
}
