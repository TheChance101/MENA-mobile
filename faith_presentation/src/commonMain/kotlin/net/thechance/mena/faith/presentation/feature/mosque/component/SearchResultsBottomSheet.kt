package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_arrow_right
import mena.faith_presentation.generated.resources.ic_mosque
import mena.faith_presentation.generated.resources.kilometer_unit
import mena.faith_presentation.generated.resources.mosque_details
import mena.faith_presentation.generated.resources.mosque_image_description
import mena.faith_presentation.generated.resources.search_results
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
internal fun ScaffoldScope.SearchResultsBottomSheet(
    isVisible: Boolean,
    mosques: Flow<PagingData<MosqueUiState>>,
    onMosqueClick: (MosqueUiState) -> Unit = {},
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lazyMosques = mosques.collectAsLazyPagingItems()

    BottomSheet(
        isVisible = isVisible,
        skipPartiallyExpanded = false,
        onDismissRequest = onDismiss,
        modifier = modifier.navigationBarsPadding(),
        sheetContent = {
            SearchResultsContent(
                mosques = lazyMosques,
                onMosqueClick = onMosqueClick
            )
        }
    )
}

@Composable
private fun SearchResultsContent(
    mosques: LazyPagingItems<MosqueUiState>,
    onMosqueClick: (MosqueUiState) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Theme.spacing._16)
            .padding(top = Theme.spacing._16)
    ) {
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._12),
            text = stringResource(Res.string.search_results),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
            contentPadding = PaddingValues(bottom = Theme.spacing._12),
        ) {
            items(count = mosques.itemCount) { index ->
                val mosque = mosques[index]
                mosque?.let {
                    SearchResultItem(
                        mosque = it,
                        onMosqueClick = onMosqueClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    mosque: MosqueUiState,
    onMosqueClick: (MosqueUiState) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onMosqueClick(mosque) }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_mosque),
            contentDescription = stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
                .padding(Theme.spacing._12)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
            modifier = Modifier
                .weight(1f)
                .padding(start = Theme.spacing._8, end = Theme.spacing._4)
        ) {
            Text(
                text = mosque.name,
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "${mosque.distance} ${stringResource(Res.string.kilometer_unit)}",
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeTertiary
            )
        }

        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = stringResource(Res.string.mosque_details),
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(3) {
                    SearchResultItem(
                        mosque = MosqueUiState(
                            id = Uuid.parse("1e6f8a10-7dec-11d0-a765-00a0c91e6bf1"),
                            name = "Al Eman Mosque",
                            imageUrl = "",
                            distance = 12.4,
                            coordinate = MosqueUiState.Coordinate(0.0, 0.0)
                        )
                    )
                }
            }
        }
    }
}
