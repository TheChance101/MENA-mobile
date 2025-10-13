package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainInteractionListener

object PreviewMainScreenInteractionListener : MainInteractionListener {
    override fun onDukanButtonClicked() {}
    override fun onViewMoreButtonClick() {}
    override fun onCategorySelectedClick(categoryId: String, categoryName: String) {}
    override fun onNearestDukanClick(dukanId: String) {}
    override fun onEditorPickDukanClick(dukanId: String) {}
}