package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetails
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
        Row (modifier = Modifier.fillMaxSize().padding(16.dp)) {
            DukanDetails(
                dukanName = "MINISO - Baghdad mall branch",
                dukanCategories = listOf("Accessories", "shoes", "perfume"),
                dukanLocation = "Karada, Baghdad",
                modifier = Modifier.padding(end = 8.dp).weight(1f).fillMaxHeight()
            )
            DukanDetails(
                dukanName = "MINISO - Baghdad mall branch",
                dukanCategories = listOf("Accessories", "shoes", "perfume"),
                dukanLocation = "Karada, Baghdad",
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}