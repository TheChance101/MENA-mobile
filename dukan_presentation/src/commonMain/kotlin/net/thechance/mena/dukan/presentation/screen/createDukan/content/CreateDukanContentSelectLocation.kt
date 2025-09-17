package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.address
import mena.dukan_presentation.generated.resources.dukan_location
import mena.dukan_presentation.generated.resources.enter_address_and_pick_dukan_location_on_map
import mena.dukan_presentation.generated.resources.ic_store_location
import mena.dukan_presentation.generated.resources.location
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.createDukan.content.component.Map
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateDukanContentSelectLocation(
    state: CreateDukanUiState,
    listener: CreateDukanInteractionListener
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._16)
    ) {
        item {
            MenaText(
                text = stringResource(Res.string.dukan_location),
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary
            )
        }
        item {
            MenaText(
                text = stringResource(Res.string.enter_address_and_pick_dukan_location_on_map),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
        item {
            MenaText(
                modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4),
                text = stringResource(Res.string.address),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary
            )
        }
        item {
            TextField(
                value = state.address,
                onValueChanged = {},
                placeholder = "",
                leadingIcon = painterResource(Res.drawable.ic_store_location),
                readOnly = true,
                enabled = false
            )
        }
        item {
            MenaText(
                modifier = Modifier.padding(top = Theme.spacing._12, bottom = Theme.spacing._4),
                text = stringResource(Res.string.location),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary
            )
        }
        item {
            Map(
                modifier = Modifier
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .fillMaxWidth()
                    .height(244.dp),
                isLocked = state.isMapLocked,
                anchorLocation = state.pointerLocation,
                cameraPosition = state.cameraPosition,
                onMapClick = listener::onMapClicked,
                onCameraMoved = listener::onCameraMoved,
                onEditClick = listener::onEditMapLocationClicked
            )
        }
    }
}