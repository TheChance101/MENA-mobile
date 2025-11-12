package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import org.koin.android.annotation.KoinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class DukanManagementViewmodel() : BaseViewModel<DukanManagementScreenState, Unit>(
    DukanManagementScreenState()
), DukanManagementInteractionListener {

    override fun onSearchQueryChange(query: String) {
        updateState {
            it.copy(query = query)
        }
    }

    override fun onClearQueryClicked() {
        updateState {
            it.copy(query = "")
        }
    }

    override fun onRetryClicked() {
        TODO("Not yet implemented")
    }

    override fun onSortClicked(type: DukanManagementScreenState.SortType) {
        TODO("Not yet implemented")
    }

    override fun onToggleUserStatusClicked(dukanId: Uuid, dukanStatus: Boolean) {
        TODO("Not yet implemented")
    }

    override fun mapError(throwable: Throwable): ErrorState {
        TODO("Not yet implemented")
    }
}