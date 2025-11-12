@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface SearchInteractionListener {
    fun onSearchChanged(query: String)
    fun onBackClicked()
    fun onClearSearchClicked()
    fun onDukansSelected()
    fun onProductsSelected()
    fun onDukanClicked(dukanId: Uuid)
    fun onDukanFavoriteToggled(dukanId: Uuid, isFavorite:Boolean)
    fun onProductClicked(productId: Uuid,dukanId: Uuid)
    fun onSnackBarDismissed()
}