package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.error_updating_favorites
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Cart
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
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
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
    private var shelfProductsLimitedMutableMap = mutableMapOf<String, List<ProductUiState>>()

    init {
        loadDukanDetails()
        loadCartInfo()
        loadShelvesPaging()
    }

    private fun loadCartInfo() {
        tryToExecute(
            block = { dukanCartRepository.getCartInfo(args.dukanId) },
            onError = ::onCartInfoError,
            onSuccess = ::onLoadCartSuccess
        )
    }

    private fun onCartInfoError(throwable: Throwable) {
        updateState { copy(hasProductInCart = false) }
    }


    private fun onLoadCartSuccess(cart: Cart) {
        updateState { copy(hasProductInCart = cart.totalPrice > 0.0) }
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
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.LOADED
            )
        }
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
        updateState { copy(shelfProductsLimited = emptyMap()) }
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
                    shelves = flowOf(shelves),
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
            onSuccess = ::onProductsLimitedLoaded,
            onError = ::onLoadProductsPagingError
        )
    }

    private fun updateProductsLimited(shelves: PagingData<ShelfUiState>): PagingData<ShelfUiState> {
        val maxProducts = 6
        val page = 0
        shelfProductsLimitedMutableMap = state.value.shelfProductsLimited.toMutableMap()
        return shelves.map { shelf ->
            if (shelfProductsLimitedMutableMap[shelf.id] == null) {
                val products =
                    productRepository.getProductsByShelfId(shelf.id, page, maxProducts).items
                shelfProductsLimitedMutableMap[shelf.id] = products.map { it.toUiState() }
                products.onEach {
                    updateState { copy(productQuantity = productQuantity + (it.id.toString() to it.quantityInCart)) }
                }
            }
            shelf
        }
    }

    private fun onProductsLimitedLoaded(updatedShelves: PagingData<ShelfUiState>) {
        updateState {
            copy(
                shelves = flowOf(updatedShelves),
                shelfProductsLimited = shelfProductsLimitedMutableMap
            )
        }
    }

    private fun loadProductsPaging() {
        updateState { copy(productQuantity = emptyMap()) }
        tryToCollect(
            block = ::getProductPagingFlow,
            onCollect = ::onProductsLoaded,
            onError = ::onLoadProductsPagingError
        )
    }

    private fun onLoadProductsPagingError(throwable: Throwable) {
        updateState {
            copy(
                error = throwable,
                isDukanInfoLoading = false,
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.ERROR
            )
        }
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
        tryToExecute(
            block = { updateQuantityProductPaging(products) },
            onSuccess = ::updateQuantityProductPagingSuccess
        )
    }

    private fun updateQuantityProductPaging(products: PagingData<ProductUiState>): PagingData<ProductUiState> {
        return products.map {
            if (state.value.productQuantity[it.id] == null)
                updateProductQuantityInCart(it.id, it.inCartQuantity)
            it
        }
    }

    private fun updateQuantityProductPagingSuccess(products: PagingData<ProductUiState>) {
        updateState { copy(productsShelf = flowOf(products)) }
    }

    override fun onBackClicked() {
        emitEffect(DukanDetailsEffects.NavigateBack)
    }

    override fun onShelfClicked(id: String) {
        if (state.value.shelfIdSelected == id) return
        updateState { copy(shelfIdSelected = id) }

        if (isWideImageStyle()) {
            loadProductsPaging()
        }
    }

    override fun onViewAllProductsShelfClicked(id: String, name: String) {
        updateState { copy(isConfigurationChanges = false) }
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
        updateState { copy(hasProductInCart = true) }
        updateProductQuantityInCart(productId, productQuantity)

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecute(
            block = { dukanCartRepository.addProductQuantity(domainRequest) },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    override fun onPlusClicked(
        productId: String,
        productQuantity: Int,
    ) {
        updateProductQuantityInCart(productId, productQuantity)
        updateState { copy(hasProductInCart = true) }

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)
        tryToExecuteWithDebounce(
            block = { dukanCartRepository.updateProductQuantity(domainRequest) },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    override fun onMinusClicked(
        productId: String,
        productQuantity: Int,
    ) {
        updateProductQuantityInCart(productId, productQuantity)

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)
        tryToExecuteWithDebounce(
            block = { onMinusClickedBlock(domainRequest, productQuantity, productId) },
            onError = ::onErrorUpdateProductQuantity
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
            onSuccess = {
                loadCartInfo()
            }
        )
    }

    private fun onErrorUpdateProductQuantity(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes)
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType = SnackBarType.ERROR) {
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
        updateState { copy(isConfigurationChanges = false) }
        emitEffect(DukanDetailsEffects.NavigateToProductDetails(productId, args.dukanId))
    }

    override fun onViewCartClicked() {
        updateState { copy(isConfigurationChanges = false) }
        emitEffect(DukanDetailsEffects.NavigateToCart(args.dukanId))
    }

    override fun onRetryClicked() {
        loadDukanDetails()
        loadCartInfo()
        loadShelvesPaging()
    }

    override fun onFavoriteDukanClicked(dukanId: String) {
        val currentProduct = state.value.dukanInfo
        val isCurrentlyFavorite = currentProduct.isFavorite
        updateState { copy(dukanInfo.copy(isFavorite = !isCurrentlyFavorite)) }
        tryToExecute(
            block = { dukanManagementRepository.updateFavoriteDukanStatus(currentProduct.dukanId) },
            onError = ::onErrorUpdateDukanFavorite
        )
    }

    private fun onErrorUpdateDukanFavorite(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.error_updating_favorites
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun updateProductQuantityInCart(productId: String, newQuantity: Int) {
        updateState {
            copy(
                productQuantity = productQuantity + (productId to newQuantity)
            )
        }
    }

    private fun isWideImageStyle() =
        state.value.dukanInfo.style == Style.WIDE_IMAGE


    fun refreshProducts() {
        loadCartInfo()
        if (!state.value.isConfigurationChanges) {
            loadShelvesPaging()
            updateState { copy(isConfigurationChanges = true) }
        }
    }
}