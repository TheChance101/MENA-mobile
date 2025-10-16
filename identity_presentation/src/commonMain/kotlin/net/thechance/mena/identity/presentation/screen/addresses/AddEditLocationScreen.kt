package net.thechance.mena.identity.presentation.screen.addresses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import io.github.dellisd.spatialk.geojson.Position
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location
import mena.identity_presentation.generated.resources.address
import mena.identity_presentation.generated.resources.address_type
import mena.identity_presentation.generated.resources.edit_location
import mena.identity_presentation.generated.resources.ic_add_location
import mena.identity_presentation.generated.resources.ic_address
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AddressTypeSection
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.MapSection
import net.thechance.mena.identity.presentation.screen.register.RegisterScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.maplibre.compose.camera.CameraPosition

class AddEditLocationScreen : BaseScreen<
        AddEditLocationScreenViewModel,
        AddLocationScreenUIState,
        AddEditLocationScreenUIEffect,
        AddEditLocationScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: AddLocationScreenUIState,
        listener: AddEditLocationScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = if (state.addressID == null)
                        stringResource(Res.string.add_location)
                    else
                        stringResource(Res.string.edit_location),
                    onBackClicked = listener::onClickBack
                )
            },
            bottomBar = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = Theme.spacing._16)
                        .padding(bottom = Theme.spacing._16),
                    text = stringResource(Res.string.save),
                    onClick = listener::onClickSave,
                    isEnabled = state.isSaveEnabled,
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(vertical = Theme.spacing._12)
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(color = Theme.colorScheme.background.surface)
                    .padding(horizontal = Theme.spacing._16)
            ) {

                item {
                    MapSection(
                        cameraPosition = CameraPosition(
                            target = Position(state.longitude, state.latitude),
                            zoom = 1.0
                        ),
                        onClickEdit = listener::onClickEdit,
                    )
                }

                item {
                    TextField(
                        value = state.address,
                        title = stringResource(Res.string.address),
                        onValueChanged = { newAddress ->
                            listener.onChangeAddress(newAddress)
                        },
                        readOnly = true,
                        enabled = false,
                        hint = "",
                        leadingIcon = painterResource(Res.drawable.ic_address),
                        modifier = Modifier.padding(top = Theme.spacing._12),
                    )
                }

                item {
                    AddressTypeSection(
                        selectedAddressType = state.addressType,
                        onClickAddressType = { newType ->
                            listener.onClickAddressType(newType)
                        }
                    )
                }

                item {
                    OtherAddressType(
                        selectedAddressType = state.addressType,
                        otherAddressType = state.otherAddress,
                        onChangeOtherAddressType = listener::onChangeOtherAddressType,
                    )
                }

            }
        }
    }

    override fun onEffect(
        effect: AddEditLocationScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            AddEditLocationScreenUIEffect.NavigateBack -> navigator.pop()
            AddEditLocationScreenUIEffect.NavigateToMap -> navigator.push(RegisterScreen()) //TODO : change it to map screen
        }
    }


}

@Composable
private fun OtherAddressType(
    selectedAddressType: AddressType?,
    otherAddressType: String?,
    onChangeOtherAddressType: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = selectedAddressType == AddressType.Other,
        enter = expandVertically(
            animationSpec = tween(durationMillis = 500)
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 500)
        )
    ) {

        TextField(
            value = otherAddressType ?: "",
            onValueChanged = onChangeOtherAddressType,
            title = stringResource(Res.string.address_type),
            hint = "",
            leadingIcon = painterResource(Res.drawable.ic_add_location),
            modifier = modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12)
        )
    }
}


