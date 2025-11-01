@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.search_general_error
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.SearchRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SearchViewModel(
    private val searchRepository: SearchRepository
) : BaseViewModel<SearchUiState, SearchEffect>(
    initialState = SearchUiState()
), SearchInteractionListener {

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

    override fun onSelectDukans() {
        updateState {
            copy(
                userSelectionSearchList = SearchUiState.UserSelectionSearchList.Dukans
            )
        }
        searchWithQuery(query = state.value.searchQuery)
    }

    override fun onSelectProducts() {
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

    override fun onDukanFavoriteClicked(dukanId: Uuid) {

        // Todo ( add to dukan favorites in repository of favorites user Story )

        val dukansState = state.value.dukanPagingFlow
        val favoriteMappedFlowDukans = dukansState.map { dukansPagingData ->
            dukansPagingData.map { dukan ->
                if (dukan == dukanId) dukan.copy(isFavorite = true)
                else dukan
            }
        }.cachedIn(viewModelScope)

        updateState {
            copy(
                dukanPagingFlow = favoriteMappedFlowDukans
            )
        }
    }

    override fun onProductClicked(productId: Uuid) {
        emitEffect(effect = SearchEffect.NavigateToProductDetails(productId = productId.toString()))
    }

    override fun onRetryClicked() {
        searchWithQuery(state.value.searchQuery)
    }

    private fun getSearchQueryCurrentState(query: String): SearchUiState.SearchContentState {
        return if (query.isNotBlank()) SearchUiState.SearchContentState.Complete
        else SearchUiState.SearchContentState.Idle
    }

    private fun searchWithQuery(query: String) {
        when (state.value.userSelectionSearchList) {
            SearchUiState.UserSelectionSearchList.Dukans -> getDukansByQuery(query = query)
            SearchUiState.UserSelectionSearchList.Products -> getProductsByQuery(query = query)
        }
    }

    private fun getDukansByQuery(query: String) {
        val validQuery = query.trim()
        if (validQuery.isBlank())
            return

        tryToCollect(
            block = { getDukansByQueryBlock(validQuery) },
            onCollect = ::onGetDukansByQueryCollect,
            onError = { onGetDukansByQueryError(it as Exception) },
        )
    }

    private fun getDukansByQueryBlock(validQuery: String): Flow<PagingData<SearchUiState.DukanUiState>> {
         return createPagingSourceFlow(
             mapper = { it.toSearchUiState() },
             onError = { exception -> onGetDukansByQueryError(exception) },
             block = { pageNumber, pageSize ->
                 searchRepository.findDukansByQuery(
                    query = validQuery,
                    page = pageNumber,
                    size = pageSize
                 ).items
             }
         )
    }

    private fun onGetDukansByQueryCollect(dukanPagingData:PagingData<SearchUiState.DukanUiState> ) {
        updateState {
            copy(
                dukanPagingFlow = flowOf(value = dukanPagingData),
                isInternetConnectionNotAvailable = false
            )
        }
    }

    private fun onGetDukansByQueryError(exception: Exception) {
        when (exception) {
            is NoInternetException -> updateState {
                copy(
                    searchContentState = SearchUiState.SearchContentState.Empty,
                    isInternetConnectionNotAvailable = true
                )
            }

            else -> updateState {
                copy(
                    searchContentState = SearchUiState.SearchContentState.Empty,
                    snackBarUiState = SnackBarUiState(
                        message = Res.string.search_general_error,
                        snackBarType = SnackBarType.ERROR
                    )
                )
            }
        }
    }

    private fun getProductsByQuery(query: String) {
        val validQuery = query.trim()
        if (validQuery.isBlank())
            return

        tryToCollect(
            block = { getProductsByQueryBlock(validQuery) },
            onCollect = ::onGetProductsByQuerySuccess,
            onError = { onGetProductsByQueryError(it as Exception) },
        )
    }


    private fun getProductsByQueryBlock(validQuery: String): Flow<PagingData<SearchUiState.ProductUiState>> {
        return createPagingSourceFlow(
            mapper = { it.toSearchUiState() },
            onError = { exception -> onGetProductsByQueryError(exception) },
            block = { pageNumber, pageSize ->
                searchRepository.findProductsByQuery(
                    query = validQuery,
                    page = pageNumber,
                    size = pageSize
                ).items
            }
        )
    }

    private fun onGetProductsByQuerySuccess(searchedProducts: PagingData<SearchUiState.ProductUiState>) {
        updateState {
            copy(
                productPagingFlow = flowOf(value = searchedProducts),
                isInternetConnectionNotAvailable = false
            )
        }
    }

    private fun onGetProductsByQueryError(exception: Exception) {
        when (exception) {
            is NoInternetException -> updateState {
                copy(
                    searchContentState = SearchUiState.SearchContentState.Empty,
                    isInternetConnectionNotAvailable = true
                )
            }

            else -> updateState {
                copy(
                    searchContentState = SearchUiState.SearchContentState.Empty,
                    snackBarUiState = SnackBarUiState(
                        message = Res.string.search_general_error,
                        snackBarType = SnackBarType.ERROR
                    )
                )
            }
        }
    }
}