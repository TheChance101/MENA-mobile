package net.thechance.mena.dukan.presentation.screen.dukans

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.dukans.content.DukansContent
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansEffects
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DukansScreen(
    viewModel: DukansViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            DukansEffects.NavigateBack -> navController.popBackStack()
            is DukansEffects.NavigateToDukanDetails -> navController.navigate(
                DukanRoute.DukanDetails(effect.dukanId)
            )
        }
    }

    DukansContent(
        state = state,
        listener = viewModel,
        pager = viewModel.initializedPager
    )
}

@Preview
@Composable
private fun DukansScreenPreview() {
    MenaTheme {
        DukansScreen()
    }
}
