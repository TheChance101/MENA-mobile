package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.navigation.LocalNavController
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
@OptIn(ExperimentalUuidApi::class)
@Composable
fun DukanDetailsScreen() {
    val navController = LocalNavController.current
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Details Screen",
            style = Theme.typography.title.large,
            modifier = Modifier.clickable { navController.popBackStack() }
        )
    }
}
