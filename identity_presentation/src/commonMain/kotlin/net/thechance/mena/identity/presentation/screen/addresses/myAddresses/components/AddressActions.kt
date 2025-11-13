package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_edit
import mena.identity_presentation.generated.resources.confirmation_dialog_delete
import mena.identity_presentation.generated.resources.ic_delete_address
import mena.identity_presentation.generated.resources.ic_edit_map
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddressActions(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDeleting: Boolean = false,
    isActivating: Boolean = false,
) {
    Row(modifier = modifier) {
        TextButton(
            modifier = Modifier.weight(1f),
            text = stringResource(Res.string.address_edit),
            trailingIcon = painterResource(Res.drawable.ic_edit_map),
            iconSize = Theme.spacing._16,
            onClick = onEditClick,
            isEnabled = !isDeleting && !isActivating
        )
        Spacer(
            Modifier
                .size(height = 18.dp, width = 1.dp)
                .background(color = Theme.colorScheme.stroke)
        )
        TextButton(
            modifier = Modifier.weight(1f),
            text = stringResource(Res.string.confirmation_dialog_delete),
            trailingIcon = painterResource(Res.drawable.ic_delete_address),
            onClick = onDeleteClick,
            iconSize = Theme.spacing._16,
            contentColor = Theme.colorScheme.error,
            isEnabled = !isDeleting && !isActivating
        )
    }
}

@Preview
@Composable
private fun AddressActionsPreview() {
    MenaTheme {
        AddressActions(onEditClick = {}, onDeleteClick = {})
    }
}