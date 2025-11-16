@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface MainInteractionListener {
    fun onDukanButtonClicked()
    fun onViewMoreClicked()
    fun onRetryClicked()
    fun onSnackBarDismissed()
    fun onSelectedCategoryClicked(categoryId: String, categoryName: String)

    fun onNearestDukanClicked(dukanId: String)

    fun onEditorPickDukanClicked(dukanId: String)
    fun onShopNowClicked(dukanId: Uuid)
    fun onFavoriteDukanClicked(dukanId: String)
    fun onSearchButtonClicked()
}