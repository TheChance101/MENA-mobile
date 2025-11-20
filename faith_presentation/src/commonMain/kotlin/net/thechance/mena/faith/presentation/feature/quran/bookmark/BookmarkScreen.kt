package net.thechance.mena.faith.presentation.feature.quran.bookmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.empty_state_bookmark_description
import mena.faith_presentation.generated.resources.empty_state_bookmark_image
import mena.faith_presentation.generated.resources.empty_state_bookmark_title
import mena.faith_presentation.generated.resources.ic_not_saved_book_mark
import mena.faith_presentation.generated.resources.remove_aya
import mena.faith_presentation.generated.resources.remove_aya_message
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.bookmark.component.BookmarkAppBar
import net.thechance.mena.faith.presentation.feature.quran.bookmark.component.BookmarkItems
import net.thechance.mena.faith.presentation.feature.quran.bookmark.component.EmptyBookmarkState
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components.DeleteConfirmationDialog
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import net.thechance.mena.faith.presentation.utils.extentions.isEmpty
import net.thechance.mena.faith.presentation.utils.extentions.isNotEmpty
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarkScreen(viewModel: BookmarkViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is BookmarkEffect.NavigateBack -> navController.navigateUp()
            is BookmarkEffect.NavigateToSur -> navController.navigate(Route.SurRoute)
        }
    }

    Content(
        uiState = state,
        listener = viewModel,
        snackBarState = snackBarState,
    )
}

@Composable
private fun Content(
    uiState: BookMarkUiState,
    snackBarState: SnackBarState,
    listener: BookmarkInteractionListener,
) {
    val bookmarks = uiState.bookmarks.collectAsLazyPagingItems()

    Scaffold(
        topBar = { BookmarkAppBar(listener::onBackClick) },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                isVisible = snackBarState.isVisible,
                status = snackBarState.status,
            )
        }, overlays = {
            dialog(
                isVisible = uiState.isDeleteConfirmationDialogVisible,
            ) {
                DeleteConfirmationDialog(
                    showDialog = uiState.isDeleteConfirmationDialogVisible,
                    onDeleteClick = listener::onConfirmDeleteBookmarkClick,
                    onDismiss = listener::onDismissDeleteConfirmationDialog,
                    title = stringResource(Res.string.remove_aya),
                    message = stringResource(Res.string.remove_aya_message)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Theme.colorScheme.background.surface)
                .padding(horizontal = Theme.spacing._16),
        ) {
            AnimatedVisibility(
                visible = bookmarks.isEmpty() && bookmarks.loadState.refresh !is LoadState.Loading,
                enter = fadeIn(tween()),
                exit = fadeOut(tween()),
            ) {
                EmptyBookmarksView(listener::onBackClick)
            }

            AnimatedVisibility(
                visible = bookmarks.loadState.refresh is LoadState.Loading,
                enter = fadeIn(tween()),
                exit = fadeOut(tween()),
            ) {
                LoadingView()
            }

            AnimatedVisibility(
                visible = bookmarks.isNotEmpty(),
                enter = fadeIn(tween()),
                exit = fadeOut(tween()),
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
private fun EmptyBookmarksView(onStartTilawahClick: () -> Unit) {
    EmptyBookmarkState(
        title = stringResource(Res.string.empty_state_bookmark_title),
        icon = painterResource(Res.drawable.ic_not_saved_book_mark),
        contentDescription = stringResource(Res.string.empty_state_bookmark_image),
        subTitle = stringResource(Res.string.empty_state_bookmark_description),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Theme.spacing._16),
        onClickButton = onStartTilawahClick
    )
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DotsProgressIndicator()
    }
}

@Composable
@Preview
private fun Preview() {
    MenaTheme {
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
                    override fun onDeleteBookmarkClick(bookmarkId: Int) {}
                    override fun onStartTilawahClick() {}
                    override fun onConfirmDeleteBookmarkClick() {}
                    override fun onDismissDeleteConfirmationDialog() {}
                },
                snackBarState = SnackBarState()
            )
        }
    }
}
