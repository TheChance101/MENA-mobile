package net.thechance.mena.faith.presentation.feature.quran.bookmark.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.components.SwappableCard
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookMarkUiState

@Composable
fun BookmarkItems(
    bookmarks: LazyPagingItems<BookMarkUiState.BookmarkCardUiState>,
    onRemoveBookmarkClick: (Int) -> Unit,
) {
    var currentSwipedCardId by remember { mutableIntStateOf(-1) }

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
                    currentSwipedCardId = currentSwipedCardId,
                    onSwipeStateChange = { newId -> currentSwipedCardId = newId },
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