package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
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
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.Style
import org.jetbrains.compose.resources.StringResource

class DukanDetailsViewModel(
    private val dukanManagementRepository: DukanManagementRepository,
    private val shelfRepository: ShelfRepository,
    private val productRepository: ProductRepository,
    private val dukanCartRepository: CartRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DukanDetailsUiState, DukanDetailsEffects>(
    DukanDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), DukanDetailsInteractionListener {

    private val args = savedStateHandle.toRoute<DukanRoute.DukanDetails>()

    init {
        loadDukanDetails()
    }

    private fun loadDukanDetails() {
        tryToExecute(
            onStart = ::onLoadDukanDetailsStart,
            block = { dukanManagementRepository.getDukanDetailsByDukanId(args.dukanId) },
            onSuccess = ::onLoadDukanDetailsSuccess,
            onError = ::onLoadDukanDetailsError
        )
    }

    private fun onLoadDukanDetailsStart() {
        updateState {
            copy(
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.LOADING
            )
        }
    }

    private fun onLoadDukanDetailsSuccess(dukanDetails: Dukan) {
        updateState {
            copy(
                dukanInfo = dukanDetails.toUiState(),
                isDukanInfoLoading = false,
            )
        }
        loadShelvesPaging()
    }

    private fun onLoadDukanDetailsError(throwable: Throwable) {
        updateState {
            copy(
                isDukanInfoLoading = false,
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.ERROR
            )
        }
    }

    private fun loadShelvesPaging() {
        tryToCollect(
            block = ::getShelvesPagingFlow,
            onCollect = ::onShelvesLoaded
        )
    }

    private fun getShelvesPagingFlow(): Flow<PagingData<ShelfUiState>> {
        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            shelfRepository.getShelvesByDukanId(
                dukanId = args.dukanId,
                pageNumber = pageNumber,
                pageSize = pageSize
            ).items
        }
    }

    private fun onShelvesLoaded(shelves: PagingData<ShelfUiState>) {
        if (isWideImageStyle()) {
            updateState {
                copy(
                    shelves = flowOf(shelves)
                )
            }
            loadProductsPaging()
        } else {
            loadProductsLimited(shelves)
        }
    }

    private fun loadProductsLimited(shelves: PagingData<ShelfUiState>) {
        tryToExecute(
            block = { updateProductsLimited(shelves) },
            onSuccess = ::onProductsLimitedLoaded
        )
    }

    private fun updateProductsLimited(
        shelves: PagingData<ShelfUiState>
    ): PagingData<ShelfUiState> {
        return shelves.map { shelf ->
            val products = getProductsLimitedByShelfId(shelf.id)
            shelf.copy(products = products)
        }.filter { it.products.isNotEmpty() }
    }

    private suspend fun getProductsLimitedByShelfId(shelfId: String): List<ProductUiState> {
        val maxProducts = 6
        val page = 0
        val product = productRepository.getProductsByShelfId(shelfId, page, maxProducts).items
        return product.map { it.toUiState() }
    }

    private fun onProductsLimitedLoaded(updatedShelves: PagingData<ShelfUiState>) {
        updateState {
            copy(
                shelves = flowOf(updatedShelves),
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.LOADED
            )
        }
    }

    private fun loadProductsPaging() {
        tryToCollect(
            block = ::getProductPagingFlow,
            onCollect = ::onProductsLoaded
        )
    }

    private fun getProductPagingFlow(): Flow<PagingData<ProductUiState>> {
        val shelfId = state.value.shelfIdSelected
        if (shelfId.isNullOrEmpty()) {
            return flowOf(PagingData.empty())
        }

        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            productRepository.getProductsByShelfId(
                shelfId = shelfId,
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<ProductUiState>) {
        updateState {
            copy(
                productsShelf = flowOf(products),
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.LOADED
            )
        }
    }

    override fun onBackClicked() {
        emitEffect(DukanDetailsEffects.NavigateBack)
    }

    override fun onShelfClicked(id: String) {
        updateState { copy(shelfIdSelected = id) }

        if (isWideImageStyle()) {
            loadProductsPaging()
        }
    }

    override fun onViewAllProductsShelfClicked(id: String, name: String) {
        emitEffect(
            DukanDetailsEffects.NavigateToViewAllShelfProducts(
                id = id,
                name = name,
                dukanId = args.dukanId
            )
        )
    }

    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {
        emitEffect(DukanDetailsEffects.NavigateToViewDukanOnMap(latitude, longitude))
    }

    override fun onAddToCartClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecute(
            block = { addToCartBlock(domainRequest, productQuantity) },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    private suspend fun addToCartBlock(
        domainRequest: UpdateProductCartQuantityParams,
        productQuantity: Int
    ) {
        if (productQuantity == 1) dukanCartRepository.addProductQuantity(domainRequest)
        else dukanCartRepository.updateProductQuantity(domainRequest)
    }

    override fun onPlusClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = { dukanCartRepository.updateProductQuantity(domainRequest) },
        )
    }

    override fun onMinusClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = { onMinusClickedBlock(domainRequest, productQuantity, productId) },
        )
    }

    private suspend fun onMinusClickedBlock(
        domainRequest: UpdateProductCartQuantityParams,
        productQuantity: Int,
        productId: String
    ) {
        if (productQuantity == 0) deleteProductFromCart(productId)
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

    override fun onProductClicked(productId: String) {
        emitEffect(DukanDetailsEffects.NavigateToProductDetails(productId, args.dukanId))
    }

    override fun onViewCartClicked() {
        emitEffect(DukanDetailsEffects.NavigateToCart(args.dukanId))
    }

    override fun onRetryClicked() {
        loadDukanDetails()
    }

    private fun isWideImageStyle() =
        state.value.dukanInfo.style == Style.WIDE_IMAGE

    fun refreshProducts() {
        loadDukanDetails()
    }


}
