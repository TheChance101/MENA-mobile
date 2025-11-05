package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_connection
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class ShelfDetailsViewModel(
    private val productRepository: ProductRepository,
    private val dukanCartRepository: CartRepository,
    private val dukanManagementRepository: DukanManagementRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ShelfDetailsUiState, ShelfDetailsEffects>(
    ShelfDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), ShelfDetailsInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ShelfDetails>()

    init {
        updateState { copy(shelfName = args.shelfName) }
        loadDukanDetails()
        loadProductsFromRepository()
    }

    private fun loadDukanDetails() {
        tryToExecute(
            block = { dukanManagementRepository.getDukanDetailsByDukanId(args.dukanId) },
            onSuccess = ::onLoadDukanDetailsSuccess,
        )
    }

    private fun onLoadDukanDetailsSuccess(dukanDetails: Dukan) {
        updateState {
            copy(
                dukanStyle = dukanDetails.style.toShelfStyle(),
                dukancolor = dukanDetails.color.toUiColor().color
            )
        }
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
                shelfId = args.shelfId,
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<ShelfDetailsUiState.ProductUiState>) {
        updateState {
            copy(productsShelf = flowOf(products))
        }
    }

    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest =
            ShelfDetailsUiState.ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecute(
            block = {
                addToCartBlock(
                    domainRequest = domainRequest,
                    productQuantity = productQuantity
                )
            },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    private suspend fun addToCartBlock(
        domainRequest: UpdateProductCartQuantityParams,
        productQuantity: Int
    ) {
        if (productQuantity == 1) dukanCartRepository.addProductQuantity(domainRequest)
        dukanCartRepository.updateProductQuantity(domainRequest)
    }

    override fun onPlusClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest =
            ShelfDetailsUiState.ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = { dukanCartRepository.updateProductQuantity(domainRequest) },
        )
    }

    override fun onMinusClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest =
            ShelfDetailsUiState.ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = {
                onMinusClickedBlock(
                    productId = productId,
                    productQuantity = productQuantity,
                    domainRequest = domainRequest
                )
            },
        )
    }

    private suspend fun onMinusClickedBlock(
        productQuantity: Int,
        productId: String,
        domainRequest: UpdateProductCartQuantityParams
    ) {
        if (productQuantity == 1) deleteProductFromCart(productId)
        else dukanCartRepository.updateProductQuantity(domainRequest)
    }

    private fun deleteProductFromCart(productId: String) {
        tryToExecute(
            block = {
                dukanCartRepository.deleteProductFromCart(
                    dukanId = args.dukanId,
                    productId = productId
                )
            },
        )
    }

    private fun onErrorUpdateProductQuantity(throwable: Throwable) {
        if (throwable is NoInternetException) {
            showSnackBar(
                message = Res.string.no_internet_connection,
                type = SnackBarType.ERROR
            )
        }
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                )
            )
        }
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(
                snackBarState = null
            )
        }
    }

    override fun onViewCartClicked() {
        emitEffect(ShelfDetailsEffects.NavigateToCart(args.dukanId))
    }

    override fun onProductClicked(productId: String) {
        emitEffect(ShelfDetailsEffects.NavigateToProductDetails(productId, args.dukanId))
    }

    fun refreshProducts() {
        loadProductsFromRepository()
    }

}