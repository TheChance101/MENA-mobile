@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.search.SearchInteractionListener
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object PreviewSearchInteractionListener: SearchInteractionListener {
    override fun onSearchChanged(query: String) {}
    override fun onBackClicked() {}
    override fun onClearSearchClicked() {}
    override fun onRetryClicked() {}
    override fun onSelectDukans() {}
    override fun onSelectProducts() {}
    override fun onDukanClicked(dukanId: Uuid) {}
    override fun onDukanFavoriteClicked(dukanId: Uuid) {}
    override fun onProductClicked(productId: Uuid) {}
    override fun onSnackBarDismissed() {}
}