@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.error_updating_favorites
import mena.dukan_presentation.generated.resources.search_general_error
import net.thechance.mena.dukan.domain.entity.ProductSearch
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.model.DukanPreview
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.SearchRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val dukanManagementRepository: DukanManagementRepository,
    defaultDispatcher: CoroutineDispatcher,
) : BaseViewModel<SearchUiState, SearchEffect>(
    initialState = SearchUiState(),
    defaultDispatcher = defaultDispatcher
), SearchInteractionListener {

    private val dukanSearchResultsFlow: MutableStateFlow<PagingData<SearchUiState.DukanUiState>> =
        MutableStateFlow(PagingData.empty())

    override fun onSearchChanged(query: String) {
        updateState {
            copy(
                searchQuery = query,
                searchContentState = getSearchQueryCurrentState(query = query)
            )
        }
        searchWithQuery(query = query)
    }

    override fun onBackClicked() {
        emitEffect(effect = SearchEffect.NavigateBack)
    }

    override fun onClearSearchClicked() {
        updateState {
            copy(
                searchQuery = "",
                searchContentState = SearchUiState.SearchContentState.Idle,
            )
        }
    }

    override fun onDukansSelected() {
        if (state.value.userSelectionSearchList == SearchUiState.UserSelectionSearchList.Dukans)
            return
        updateState {
            copy(
                userSelectionSearchList = SearchUiState.UserSelectionSearchList.Dukans
            )
        }
        searchWithQuery(query = state.value.searchQuery)
    }

    override fun onProductsSelected() {
        if (state.value.userSelectionSearchList == SearchUiState.UserSelectionSearchList.Products)
            return
        updateState {
            copy(
                userSelectionSearchList = SearchUiState.UserSelectionSearchList.Products
            )
        }
        searchWithQuery(query = state.value.searchQuery)
    }

    override fun onDukanClicked(dukanId: Uuid) {
        emitEffect(effect = SearchEffect.NavigateToDukanDetails(dukanId = dukanId.toString()))
    }

    override fun onDukanFavoriteToggled(dukanId: Uuid, isFavorite: Boolean) {
        onDukanFavoriteToggleSuccess(dukanId = dukanId)

        tryToExecute(
            block = { onDukanFavoriteToggleBlock(dukanId) },
            onError = { onDukanFavoriteToggleError(it as Exception) }
        )
    }

    override fun onProductClicked(productId: Uuid, dukanId: Uuid) {
        emitEffect(effect = SearchEffect.NavigateToProductDetails(productId = productId.toString(),dukanId = dukanId.toString()))
    }

    override fun onSnackBarDismissed() {
        updateState {
            copy(
                snackBarUiState = null
            )
        }
    }


    private fun getSearchQueryCurrentState(query: String): SearchUiState.SearchContentState {
        return if (query.isNotBlank())
            SearchUiState.SearchContentState.Complete
        else
            SearchUiState.SearchContentState.Idle
    }

    private fun searchWithQuery(query: String) {
        val validQuery = query.trim()
        if (validQuery.isBlank())
            return

        when (state.value.userSelectionSearchList) {
            SearchUiState.UserSelectionSearchList.Dukans -> getDukansByQuery(query = validQuery)
            SearchUiState.UserSelectionSearchList.Products -> getProductsByQuery(query = validQuery)
        }
    }

    private fun getDukansByQuery(query: String) {
        tryToCollect(
            block = { getDukansByQueryBlock(query) },
            onCollect = ::onGetDukansByQueryCollect,
            onError = { onGetDukansByQueryError(it as Exception) },
        )
    }

    private fun getDukansByQueryBlock(validQuery: String): Flow<PagingData<SearchUiState.DukanUiState>> {
        return createPagingSourceFlow(
            mapper = DukanPreview::toSearchUiState,
            onError = { exception -> onGetDukansByQueryError(exception) },
            block = { pageNumber, _ ->
                searchRepository.findDukansByQuery(
                    query = validQuery,
                    page = pageNumber,
                    size = 15
                ).items
            }
        )
    }

    private fun onGetDukansByQueryCollect(dukanPagingData: PagingData<SearchUiState.DukanUiState>) {
        dukanSearchResultsFlow.value = dukanPagingData
        updateState {
            copy(
                dukanPagingFlow = dukanSearchResultsFlow,
            )
        }
    }

    private fun onGetDukansByQueryError(exception: Exception) {
        when (exception) {
            is NoInternetException -> handleNoInternetException()
            else -> handleGeneralSearchException()
        }
    }

    private fun getProductsByQuery(query: String) {
        tryToCollect(
            block = { getProductsByQueryBlock(query) },
            onCollect = ::onGetProductsByQuerySuccess,
            onError = { onGetProductsByQueryError(it as Exception) },
        )
    }

    private fun getProductsByQueryBlock(validQuery: String): Flow<PagingData<SearchUiState.ProductUiState>> {
        return createPagingSourceFlow(
            mapper = ProductSearch::toSearchUiState,
            onError = { exception -> onGetProductsByQueryError(exception) },
            block = { pageNumber, _ ->
                searchRepository.findProductsByQuery(
                    query = validQuery,
                    page = pageNumber,
                    size = 15
                ).items
            }
        )
    }

    private fun onGetProductsByQuerySuccess(searchedProducts: PagingData<SearchUiState.ProductUiState>) {
        updateState {
            copy(
                productPagingFlow = flowOf(value = searchedProducts),
            )
        }
    }

    private fun onGetProductsByQueryError(exception: Exception) {
        when (exception) {
            is NoInternetException -> handleNoInternetException()
            else -> handleGeneralSearchException()
        }
    }

    private suspend fun onDukanFavoriteToggleBlock(dukanId: Uuid) {
        dukanManagementRepository.updateFavoriteDukanStatus(dukanId = dukanId.toString())
    }

    private fun onDukanFavoriteToggleSuccess( dukanId: Uuid) {
        val currentSearchDukans = dukanSearchResultsFlow.value
        val updatedSearchDukans = currentSearchDukans.map { dukanItem ->
                if (dukanItem.id == dukanId) dukanItem.copy(isFavorite = !dukanItem.isFavorite)
                else dukanItem
            }
        dukanSearchResultsFlow.value = updatedSearchDukans
        updateState { copy(dukanPagingFlow = dukanSearchResultsFlow ) }
    }

    private fun onDukanFavoriteToggleError(exception: Exception) {
        when (exception) {
            is NoInternetException -> handleNoInternetException()
            else -> handleGeneralFavoriteDukanException()
        }
    }

    private fun handleGeneralFavoriteDukanException() {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = Res.string.error_updating_favorites,
                    snackBarType = SnackBarType.ERROR
                )
            )
        }
    }

    private fun handleNoInternetException() {
        updateState {
            copy(
                searchContentState = SearchUiState.SearchContentState.NoInternet
            )
        }
    }

    private fun handleGeneralSearchException() {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = Res.string.search_general_error,
                    snackBarType = SnackBarType.ERROR
                )
            )
        }
    }
}