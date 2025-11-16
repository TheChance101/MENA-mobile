package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_dukan_placholder
import net.thechance.mena.admin_panel.resources.image
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(Res.string.image),
        error = painterResource(Res.drawable.ic_dukan_placholder),
        placeholder = painterResource(Res.drawable.ic_dukan_placholder),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 64.dp, height = 44.dp)
            .clip(RoundedCornerShape(Theme.radius.sm))
    )
}