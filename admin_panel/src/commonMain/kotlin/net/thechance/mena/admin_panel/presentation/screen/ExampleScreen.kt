package net.thechance.mena.admin_panel.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.app_name
import net.thechance.mena.admin_panel.resources.mena_logo
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExampleScreen(){
    var showContent by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(Theme.colorScheme.background.surface)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me", style = Theme.typography.body.large)
        }
        AnimatedVisibility(showContent) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(Res.drawable.mena_logo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    stringResource(Res.string.app_name) ,
                    style = Theme.typography.body.medium
                )
            }
        }
    }
}
