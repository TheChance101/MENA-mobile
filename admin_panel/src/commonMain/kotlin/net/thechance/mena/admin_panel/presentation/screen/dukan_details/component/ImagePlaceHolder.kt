package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_img
import net.thechance.mena.admin_panel.resources.dukan_placholder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ImagePlaceHolder(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(Res.drawable.dukan_placholder),
        contentDescription = stringResource(Res.string.dukan_img),
        contentScale = ContentScale.Crop
    )
}