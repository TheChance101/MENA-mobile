package net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.calibrate_device
import mena.faith_presentation.generated.resources.calibrate_device_animation
import mena.faith_presentation.generated.resources.continue_btn
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.motion_configuration
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.qiblah.component.IslamicPattern
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalibrateDeviceScreen(
    viewModel: CalibrateDeviceViewModel = koinViewModel()
) {
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is CalibrateDeviceEffect.NavigateBack -> navController.navigateUp()
            is CalibrateDeviceEffect.NavigateToQiblah -> navController.navigate(Route.CompassRoute)
        }
    }

    Content(listener = viewModel)
}

@Composable
private fun Content(listener: CalibrateDeviceInteractionListener) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.calibrate_device),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        tint = Theme.colorScheme.primary.primary,
                        contentDescription = stringResource(Res.string.arrow_left)
                    )
                },
                onLeadingClick = listener::onBackClick
            )
        },
        bottomBar = { ContinueButton(listener = listener) }
    ) { ConfigurationMessage() }
}

@Composable
private fun ContinueButton(
    listener: CalibrateDeviceInteractionListener
) {
    Button(
        onClick = listener::onContinueClick,
        content = {
            Text(
                text = stringResource(Res.string.continue_btn),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.primary.onPrimary
            )
        },
        containerColor = Theme.colorScheme.primary.primary,
        contentPadding = PaddingValues(vertical = 13.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = Theme.spacing._16,
                end = Theme.spacing._16,
                bottom = Theme.spacing._24
            )
            .clip(shape = RoundedCornerShape(Theme.radius.md))
    )
}

@Composable
private fun ConfigurationMessage() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        IslamicPattern(
            modifier = Modifier.align(Alignment.BottomStart)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = Theme.spacing._16)
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .background(
                        color = Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(Theme.radius.xl)
                    )
            ) {
                AsyncImage(
                    model = Res.getUri("drawable/loading_qiblah.gif"),
                    contentDescription = stringResource(Res.string.calibrate_device_animation),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                text = stringResource(Res.string.motion_configuration),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(vertical = Theme.spacing._16)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                listener = object : CalibrateDeviceInteractionListener {
                    override fun onBackClick() {}
                    override fun onContinueClick() {}
                }
            )
        }
    }
}
