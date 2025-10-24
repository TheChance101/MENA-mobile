package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.DUKAN_COLOR
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.DUKAN_STYLE
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.SHELF_ID
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.SHELF_NAME
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ShelfDetailsViewModel(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ShelfDetailsUiState, ShelfDetailsEffects>(
    ShelfDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), ShelfDetailsInteractionListener {
    val shelfId: String = requireNotNull(savedStateHandle[SHELF_ID])
    val shelfName: String = requireNotNull(savedStateHandle[SHELF_NAME])
    val dukanStyle: String = requireNotNull(savedStateHandle[DUKAN_STYLE])
    val dukancolor: Long = requireNotNull(savedStateHandle[DUKAN_COLOR])


    init {
        updateState {
            copy(
                shelfName = this@ShelfDetailsViewModel.shelfName,
                dukanStyle = ShelfDetailsUiState.Style.valueOf(this@ShelfDetailsViewModel.dukanStyle),
                dukancolor = this@ShelfDetailsViewModel.dukancolor
            )
        }
        loadProductsFromRepository()
    }

    private fun loadProductsFromRepository() {
        tryToCollect(
            block = ::createPagingSource,
            onCollect = ::onProductsLoaded
        )
    }

    private fun createPagingSource(): Flow<PagingData<ShelfDetailsUiState.ProductUiState>> {
        return createPagingSourceFlow(
            mapper = { it.toUiState() }
        ) { pageNumber, pageSize ->
            productRepository.getProductsByShelfId(
                shelfId = shelfId,
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<ShelfDetailsUiState.ProductUiState>) =
        updateState {
            copy(productsShelf = flowOf(products))
        }

    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClick(productId: String) {
        // ToDO("Not yet implemented")
    }
}