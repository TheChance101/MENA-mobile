package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.pick_on_map
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.GpsFabButton
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.PickLocationMap
import org.jetbrains.compose.resources.stringResource

@Composable
fun PickLocationScreen(
    state: PickLocationScreenUIState,
    listener: PickLocationScreenInteractionListener
) {
    Scaffold(
        statusBarColor = Theme.colorScheme.background.surfaceLow,
        topBar = {
            AppBar(
                title = stringResource(Res.string.pick_on_map),
                onLeadingClick = listener::onClickBack,
            )
        },
    ) {
        PickLocationMap(
            currentLocation = state.mosqueLocation,
            animateToCurrentLocation = state.animateToCurrentLocation,
            showAnchor = state.showAnchor,
            onClickMap = listener::onClickMap,
            onMoveCamera = listener::onMoveCamera,
        ) {
            Column(
                Modifier.padding(Theme.spacing._16).fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                GpsFabButton(
                    onClick = listener::onClickGps,
                    isLoading = state.isGpsButtonLoading,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                PrimaryButton(
                    text = stringResource(Res.string.confirm),
                    onClick = listener::onClickConfirm,
                    isEnabled = state.isConfirmEnabled,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}