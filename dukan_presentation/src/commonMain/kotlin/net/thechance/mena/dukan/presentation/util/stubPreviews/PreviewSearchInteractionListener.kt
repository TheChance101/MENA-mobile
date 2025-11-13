@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.search.SearchInteractionListener
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object PreviewSearchInteractionListener: SearchInteractionListener {
    override fun onSearchChanged(query: String) {}
    override fun onBackClicked() {}
    override fun onClearSearchClicked() {}
    override fun onDukansSelected() {}
    override fun onProductsSelected() {}
    override fun onDukanClicked(dukanId: Uuid) {}
    override fun onDukanFavoriteToggled(dukanId: Uuid, isFavorite: Boolean) {}
    override fun onProductClicked(productId: Uuid,dukanId: Uuid) {}
    override fun onSnackBarDismissed() {}
}