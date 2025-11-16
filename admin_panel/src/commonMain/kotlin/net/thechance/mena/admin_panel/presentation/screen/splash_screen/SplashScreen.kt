package net.thechance.mena.admin_panel.presentation.screen.splash_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.mena
import net.thechance.mena.admin_panel.resources.mena_logo
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Theme.colorScheme.background.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(Res.drawable.mena_logo),
            contentDescription = stringResource(Res.string.mena),
            modifier = Modifier.padding(bottom = 2.dp).size(88.dp)
        )
        Text(
            text = stringResource(Res.string.mena),
            textAlign = TextAlign.Center,
            style = Theme.typography.appName,
            color = Color(0xFF27374D)
        )
    }
}