package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import androidx.compose.runtime.Composable
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsAppBar

@Composable
internal fun DukanDetailsScreen() {
    PanelScaffold(
        topBar = {
            DukanDetailsAppBar(
                onBackBtnClicked = {},
                dukanStatus = DukanDetailsScreenState.DukanStatus.ACTIVE,
                onChangeDukanStatusBtnClicked = { }
            )
        }
    ) {

    }
}