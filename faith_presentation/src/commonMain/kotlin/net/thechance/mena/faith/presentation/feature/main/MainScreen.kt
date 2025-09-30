package net.thechance.mena.faith.presentation.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.navigate_to_sur_screen
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.SurRoute
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen() {

    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Theme.spacing._24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = { navController.navigate(SurRoute) },
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.bgSuccess),
        ) {

            Text(
                text = stringResource(Res.string.navigate_to_sur_screen),
                style = Theme.typography.body.large,
                modifier = Modifier.padding(Theme.spacing._16)
            )
        }
    }
}
