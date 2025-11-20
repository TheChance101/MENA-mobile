package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.location
import mena.identity_presentation.generated.resources.pick_on_map
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components.AddLocationMap
import org.jetbrains.compose.resources.stringResource
import org.maplibre.compose.camera.CameraPosition
import sv.lib.squircleshape.SquircleShape

@Composable
fun MapSection(
    isMapClickable: Boolean,
    cameraPosition: CameraPosition,
    onClickEdit: () -> Unit,
    onClickMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(top = Theme.spacing._8, bottom = Theme.spacing._4)
        ) {
            Text(
                text = stringResource(Res.string.location),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary
            )
            Text(
                text = stringResource(Res.string.pick_on_map),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
        AddLocationMap(
            isMapClickable = isMapClickable,
            modifier = Modifier
                .clip(SquircleShape(Theme.radius.md))
                .fillMaxWidth()
                .height(244.dp),
            cameraPosition = cameraPosition,
            onEditClick = onClickEdit,
            onClickMap = onClickMap
        )
    }
}