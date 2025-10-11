package net.thechance.mena.identity.presentation.screen.addresses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import io.github.dellisd.spatialk.geojson.Position
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location
import mena.identity_presentation.generated.resources.address
import mena.identity_presentation.generated.resources.address_type
import mena.identity_presentation.generated.resources.edit_location
import mena.identity_presentation.generated.resources.home
import mena.identity_presentation.generated.resources.ic_add_location
import mena.identity_presentation.generated.resources.ic_address
import mena.identity_presentation.generated.resources.ic_home
import mena.identity_presentation.generated.resources.ic_marker
import mena.identity_presentation.generated.resources.ic_office
import mena.identity_presentation.generated.resources.location
import mena.identity_presentation.generated.resources.office
import mena.identity_presentation.generated.resources.other
import mena.identity_presentation.generated.resources.pick_on_map
import mena.identity_presentation.generated.resources.save
import mena.identity_presentation.generated.resources.type
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.Map
import net.thechance.mena.identity.presentation.screen.register.RegisterScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.maplibre.compose.camera.CameraPosition

class AddLocationScreen :
    BaseScreen<AddLocationScreenViewModel,
            AddLocationScreenUIState,
            AddLocationScreenUIEffect,
            AddLocationScreenInteractionListener>()
    {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: AddLocationScreenUIState, listener: AddLocationScreenInteractionListener
    ) {
        Scaffold(topBar = {
            AuthAppBar(
                title = if (state.addressID == null) stringResource(Res.string.add_location)
                else stringResource(Res.string.edit_location),
                onBackClicked = listener::onClickBack
            )
        }, bottomBar = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth().padding(Theme.spacing._16),
                text = stringResource(Res.string.save),
                onClick = listener::onClickSave,
                isEnabled = state.isSaveEnabled,
                isLoading = state.isLoading,
                contentPadding = PaddingValues(vertical = Theme.spacing._12)
            )
        }) {
            LazyColumn(
                modifier = Modifier.background(color = Theme.colorScheme.background.surface)
                    .padding(horizontal = 16.dp).padding(top = 8.dp)
            ) {

                item {
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
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
                    Map(

                        modifier = Modifier.clip(RoundedCornerShape(Theme.radius.md)).fillMaxWidth()
                            .height(244.dp),
                        cameraPosition = CameraPosition(
                            target = Position(state.longitude, state.latitude),
                            zoom = 1.0
                        ),
                        onEditClick = listener::onClickEdit,
                    )
                }

                item {

                    TextField(
                        value = state.address,
                        title = stringResource(Res.string.address),
                        onValueChanged = { newAddress ->
                            listener.onAddressChanged(newAddress)
                        },
                        readOnly = true,
                        enabled = false,
                        hint = "",
                        leadingIcon = painterResource(Res.drawable.ic_address),
                        modifier = Modifier.padding(top = Theme.spacing._12),

                        )
                }

                item {
                    Text(
                        text = stringResource(Res.string.type),
                        style = Theme.typography.title.small,
                        color = Theme.colorScheme.shadePrimary,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AddressTypeItem(
                            addressIcon = painterResource(Res.drawable.ic_home),
                            addressType = stringResource(Res.string.home),
                            onAddressClick = { listener.onClickAddressType(AddressType.Home) },
                            isSelected = state.addressType == AddressType.Home,
                        )
                        AddressTypeItem(
                            addressIcon = painterResource(Res.drawable.ic_office),
                            addressType = stringResource(Res.string.office),
                            onAddressClick = { listener.onClickAddressType(AddressType.Office) },
                            isSelected = state.addressType == AddressType.Office,
                        )
                        AddressTypeItem(
                            addressIcon = painterResource(Res.drawable.ic_marker),
                            addressType = stringResource(Res.string.other),
                            onAddressClick = { listener.onClickAddressType(AddressType.Other) },
                            isSelected = state.addressType == AddressType.Other,
                        )
                    }

                }

                item {
                    AnimatedVisibility(
                        visible = state.addressType == AddressType.Other,
                        enter = expandVertically(
                            animationSpec = tween(
                                durationMillis = 500,
                            )
                        ),
                        exit = shrinkVertically (
                            tween(
                                durationMillis = 500,
                            )
                        )
                    ) {

                        TextField(
                            value = state.otherAddress ?: "",
                            onValueChanged = { newType ->
                                listener.onOtherAddressTypeChanged(newType)
                            },
                            title = stringResource(Res.string.address_type),
                            hint = "",
                            leadingIcon = painterResource(Res.drawable.ic_add_location),
                            modifier = Modifier.padding(top = Theme.spacing._12)
                        )
                    }
                }

            }
        }
    }

    override fun onEffect(
        effect: AddLocationScreenUIEffect, navigator: Navigator
    ) {
        when (effect) {
            AddLocationScreenUIEffect.NavigateBack -> navigator.pop()
            AddLocationScreenUIEffect.NavigateToMap -> navigator.push(RegisterScreen()) //TODO : change it to map screen
        }
    }


}


@Composable
fun AddressTypeItem(
    addressIcon: Painter,
    addressType: String,
    isSelected: Boolean,
    onAddressClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.brand.brand
        else Theme.colorScheme.background.surfaceLow
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.primary.onPrimary
        else Theme.colorScheme.primary.primary
    )

    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = addressIcon,
            tint = iconColor,
            contentDescription = "Address Icon",
            modifier = Modifier.background(backgroundColor, shape = CircleShape).clickable {
                    onAddressClick()
                }.padding(20.dp)


        )
        Text(
            text = addressType,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.clickable {
                    onAddressClick()
            })
    }

}

