package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.DukanDetailsScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.back_btn
import net.thechance.mena.admin_panel.resources.dukan_details
import net.thechance.mena.admin_panel.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DukanDetailsAppBar(
    onBackBtnClicked: () -> Unit,
    dukanStatus: DukanDetailsScreenState.DukanStatus,
    onChangeDukanStatusBtnClicked: () -> Unit,
    isActiveDukanButtonLoading: Boolean,
    modifier: Modifier = Modifier
) {
    AppBar(
        modifier = modifier.background(Theme.colorScheme.background.surfaceLow),
        title = stringResource(Res.string.dukan_details),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        onLeadingClick = onBackBtnClicked,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                tint = Theme.colorScheme.shadePrimary,
                contentDescription = stringResource(Res.string.back_btn),
            )
        },
        trailingContent = {
            when (dukanStatus) {
                DukanDetailsScreenState.DukanStatus.DEACTIVE -> {
                    PrimaryButton(
                        modifier = Modifier.width(168.dp),
                        text = stringResource(dukanStatus.text),
                        onClick = onChangeDukanStatusBtnClicked,
                        trailingIcon = painterResource(dukanStatus.icon),
                        isLoading = isActiveDukanButtonLoading
                    )
                }

                DukanDetailsScreenState.DukanStatus.ACTIVE -> {
                    OutlinedButton(
                        modifier = Modifier.widthIn(min = 137.dp),
                        text = stringResource(dukanStatus.text),
                        onClick = onChangeDukanStatusBtnClicked,
                        trailingIcon = painterResource(dukanStatus.icon)
                    )
                }
            }
        }
    )
}