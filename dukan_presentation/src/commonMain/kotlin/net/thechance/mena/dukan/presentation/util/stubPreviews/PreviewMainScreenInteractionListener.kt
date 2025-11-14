@file:OptIn(ExperimentalUuidApi::class)
package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainInteractionListener
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object PreviewMainScreenInteractionListener : MainInteractionListener {
    override fun onDukanButtonClicked() {}
    override fun onViewMoreClicked() {}
    override fun onRetryClicked() {}
    override fun onSnackBarDismissed() {}
    override fun onSelectedCategoryClicked(categoryId: String, categoryName: String) {}
    override fun onNearestDukanClicked(dukanId: String) {}
    override fun onEditorPickDukanClicked(dukanId: String) {}
    override fun onShopNowClicked(dukanId: Uuid) {}
    override fun onFavoriteDukanClicked(dukanId: String) {}
    override fun onSearchButtonClicked() {}
}