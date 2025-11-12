package net.thechance.mena.admin_panel.presentation.screen.SplashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.mena
import net.thechance.mena.admin_panel.resources.mena_logo
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(color = Theme.colorScheme.background.surface)
    ){
        Image(
            painter = painterResource(Res.drawable.mena_logo),
            contentDescription = stringResource(Res.string.mena),
            modifier = Modifier.align(Alignment.Center).size(88.dp)
        )
    }
}