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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.aya
import mena.faith_presentation.generated.resources.back
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_clear
import mena.faith_presentation.generated.resources.ic_outline_search
import mena.faith_presentation.generated.resources.ic_search
import mena.faith_presentation.generated.resources.ic_search_warning
import mena.faith_presentation.generated.resources.no_results_found_subtitle
import mena.faith_presentation.generated.resources.no_results_found_title
import mena.faith_presentation.generated.resources.shadow
import mena.faith_presentation.generated.resources.start_searching_subtitle
import mena.faith_presentation.generated.resources.start_searching_title
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.component.DotSeparator
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
    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            SearchEffect.NavigateBack -> {}
            is SearchEffect.NavigateToSurah -> {}
        }
    }
    Content(state = state, listener = viewModel)
}

@Composable
private fun Content(
    state: SearchScreenState,
    listener: SearchInteractionListener
) {
    Scaffold(topBar = {
        SearchHeader(
            query = state.query,
            hint = state.hint,
            onQueryChange = listener::onQueryChange,
            clearQuery = listener::onClearQueryClick,
            onBackClick = listener::onBackClick,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._4)
        )
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StartOrEmptyState(
                isBlankQuery = state.query.isBlank(),
                isEmptyResult = state.searchResult.isEmpty(),
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            ResultList(
                isNotBlankQuery = state.query.isNotBlank(),
                isNotEmptyResult = state.searchResult.isNotEmpty(),
                result = state.searchResult,
                onSearchClick = listener::onSearchResultClick
            )
        }
    }
}


@Composable
private fun SearchHeader(
    query: String,
    hint: String,
    onQueryChange: (String) -> Unit,
    clearQuery: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .size(20.dp),
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back)
            )
        }
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
                    modifier = Modifier.padding(bottom = Theme.spacing._12),
                    painter = painterResource(if (isBlankQuery) Res.drawable.ic_search else Res.drawable.ic_search_warning),
                    contentDescription = stringResource(if (isBlankQuery) Res.string.start_searching_title else Res.string.no_results_found_title)
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
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._12
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(result) {
            SearchResultCard(
                surahName = it.surahName,
                ayaNumber = it.number,
                ayaText = it.content,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceLow)
                    .clickable { onSearchClick(it.surahId, it.number) }
                    .padding(Theme.spacing._12)
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
        modifier = modifier,
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