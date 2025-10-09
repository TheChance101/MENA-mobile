package net.thechance.mena.faith.presentation.feature.quran.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.aya
import mena.faith_presentation.generated.resources.ic_arrow
import mena.faith_presentation.generated.resources.ic_clear
import mena.faith_presentation.generated.resources.ic_outline_search
import mena.faith_presentation.generated.resources.ic_search
import mena.faith_presentation.generated.resources.ic_search_warning
import mena.faith_presentation.generated.resources.no_results_found_subtitle
import mena.faith_presentation.generated.resources.no_results_found_title
import mena.faith_presentation.generated.resources.shadow
import mena.faith_presentation.generated.resources.start_searching_subtitle
import mena.faith_presentation.generated.resources.start_searching_title
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.feature.quran.surah.component.getAyahTextStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchScreen(
    surahId: Int?,
    surahName: String?,
    viewModel: SearchViewModel = koinViewModel(parameters = { parametersOf(surahId, surahName) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEffect(viewModel.uiEffect) {
        when (it) {
            SearchEffect.NavigateBack -> {}
            is SearchEffect.NavigateToSurah -> {}
        }
    }
    Content(state, viewModel)
}

@Composable
private fun Content(
    state: SearchScreenState,
    listener: SearchInteractionListener
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchHeader(
            query = state.query,
            hint = state.hint,
            onQueryChange = interactionListener::onQueryChange,
            clearQuery = interactionListener::clearQuery,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable(onClick = interactionListener::onBackClick)
        )
        StartOrEmptyState(
            state.query.isBlank(),
            state.searchResult.isEmpty(),
            Modifier.fillMaxWidth().weight(1f)
        StartOrEmptyState(
            isBlankQuery = state.query.isBlank(),
            isEmptyResult = state.searchResult.isEmpty(),
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        ResultList(
            isNotBlankQuery = state.query.isNotBlank(),
            isNotEmptyResult = state.searchResult.isNotEmpty(),
            result = state.searchResult,
            onSearchClick = interactionListener::onSearchResultClick
        )
    }
}

@Composable
private fun SearchHeader(
    query: String,
    hint: String,
    onQueryChange: (String) -> Unit,
    clearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Theme.colorScheme.background.surfaceLow,
                    RoundedCornerShape(Theme.radius.md)
                )
                .padding(vertical = 14.56.dp, horizontal = 17.dp),
            painter = painterResource(Res.drawable.ic_arrow),
            contentDescription = ""
        )
        TextField(
            modifier = Modifier.weight(1f),
            value = query,
            hint = hint,
            onValueChanged = onQueryChange,
            leadingIcon = painterResource(Res.drawable.ic_outline_search),
            leadingIconTint = Theme.colorScheme.shadeSecondary,
            trailingIcon = if (query.isNotBlank()) painterResource(Res.drawable.ic_clear) else null,
            onTrailingIconClick = clearQuery
        )
    }
}

@Composable
private fun StartOrEmptyState(
    isBlankQuery: Boolean,
    isEmptyResult: Boolean,
    modifier: Modifier = Modifier
) {
    if (isBlankQuery || !isBlankQuery && isEmptyResult)
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(128.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    painter = painterResource(Res.drawable.shadow),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier.padding(bottom = 12.dp),
                    painter = painterResource(if (isBlankQuery) Res.drawable.ic_search else Res.drawable.ic_search_warning),
                    contentDescription = null
                )
            }
            Text(
                text = stringResource(if (isBlankQuery) Res.string.start_searching_title else Res.string.no_results_found_title),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
            )
            Text(
                text = stringResource(if (isBlankQuery) Res.string.start_searching_subtitle else Res.string.no_results_found_subtitle),
                style = Theme.typography.body.small,
                textAlign = TextAlign.Center,
                color = Theme.colorScheme.shadeSecondary,
            )
        }
}

@Composable
private fun ResultList(
    isNotBlankQuery: Boolean,
    isNotEmptyResult: Boolean,
    result: List<SearchResult>,
    onSearchClick: (surahId: Int, ayahId: Int) -> Unit
) {
    if (isNotBlankQuery && isNotEmptyResult) LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(result) {
            SearchResultCard(
                surahName = it.surahName,
                ayaNumber = it.number,
                ayaText = it.content,
                modifier = Modifier.clickable { onSearchClick(it.surahId, it.number) }
            )
        }
    }
}

@Composable
private fun SearchResultCard(
    surahName: String,
    ayaNumber: Int,
    ayaText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().background(
            color = Theme.colorScheme.background.surfaceLow,
            shape = RoundedCornerShape(Theme.radius.md)
        ).padding(Theme.spacing._12),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        SurahAndAyaInfo(
            surahName = surahName,
            ayaNumber = ayaNumber,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = ayaText,
            color = Theme.colorScheme.shadeSecondary,
            style = getAyahTextStyle().copy(lineHeight = 35.sp)
        )
    }
}

@Composable
private fun SurahAndAyaInfo(
    surahName: String,
    ayaNumber: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = surahName,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )
        DotSeparator()
        Text(
            text = stringResource(Res.string.aya, ayaNumber),
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )
    }
}

@Composable
private fun DotSeparator() {
    Box(
        modifier = Modifier
            .padding(horizontal = Theme.spacing._8)
            .size(3.dp)
            .background(
                color = Theme.colorScheme.shadeTertiary,
                shape = RoundedCornerShape(Theme.radius.full)
            )
    )
}