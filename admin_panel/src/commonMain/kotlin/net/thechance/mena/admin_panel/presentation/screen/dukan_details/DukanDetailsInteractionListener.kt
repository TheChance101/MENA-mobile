package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface DukanDetailsInteractionListener {
    fun onBackButtonClicked()
    fun onChangeDukanStatusButtonClicked()
    fun onNextShelvesPageRequested()
    fun onShelfSelected(shelfId: Uuid)
    fun onNextProductsPageRequested()
    fun onDeactivateDukanDialogDismissed()
    fun onDukanDeactivationButtonClicked()
    fun onDeactivateReasonChanged(reason: String)
    fun onRetry()
}