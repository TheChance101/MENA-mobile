package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainInteractionListener

object PreviewMainScreenInteractionListener : MainInteractionListener {
    override fun onDukanButtonClicked() {}
    override fun onViewMoreClicked() {}
    override fun onRetryClicked() {}
    override fun onDismissSnackBar() {}
    override fun onCategorySelectedClicked(categoryId: String, categoryName: String) {}
    override fun onNearestDukanClicked(dukanId: String) {}
    override fun onEditorPickDukanClicked(dukanId: String) {}
}