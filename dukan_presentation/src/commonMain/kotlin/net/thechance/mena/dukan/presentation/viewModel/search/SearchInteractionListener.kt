@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface SearchInteractionListener {
    fun onSearchChanged(query: String)
    fun onBackClicked()
    fun onClearSearchClicked()
    fun onRetryClicked()
    fun onSelectDukans()
    fun onSelectProducts()
    fun onDukanClicked(dukanId: Uuid)
    fun onDukanFavoriteClicked(dukanId: Uuid)
    fun onProductClicked(productId: Uuid)
    fun onSnackBarDismissed()
}