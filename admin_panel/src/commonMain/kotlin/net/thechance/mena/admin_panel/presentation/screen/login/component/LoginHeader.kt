package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login_header
import net.thechance.mena.admin_panel.resources.login_sub_title
import net.thechance.mena.admin_panel.resources.mena
import net.thechance.mena.admin_panel.resources.mena_logo
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.mena_logo),
            contentDescription = stringResource(Res.string.mena),
            modifier = Modifier.size(88.dp)
        )
        Text(
            text = stringResource(Res.string.mena),
            textAlign = TextAlign.Center,
            style = Theme.typography.appName,
            color = Color(0xFF27374D)
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(Res.string.login_header),
            style = Theme.typography.title.medium.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.2f),
                    offset = Offset(x = 0f, y = 4f),
                    blurRadius = 8f
                )
            ),
            color = Theme.colorScheme.shadePrimary,
        )
        Text(
            text = stringResource(Res.string.login_sub_title),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
        )
    }
}