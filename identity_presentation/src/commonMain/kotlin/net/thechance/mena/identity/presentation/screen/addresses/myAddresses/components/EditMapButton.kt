package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_edit
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun EditMapButton(
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier
            .padding( end = 4.dp, start = 4.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable { onEditClick() }
            .background(Color.Black)
            .padding(
                horizontal = Theme.spacing._16,
                vertical = 14.dp
            ).size(20.dp),
        painter = painterResource(Res.drawable.ic_edit),
        contentDescription = null
    )
}